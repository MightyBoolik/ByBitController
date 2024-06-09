package com.example.demo;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    @Value("${bybit.api.key}")
    private String bybitApiKey;

    @Value("${bybit.api.secret}")
    private String bybitApiSecret;

    @Value("${telegram.bot.token}")
    private String telegramBotToken;

    @Value("${telegram.chat.id}")
    private String telegramChatId;

    private final OkHttpClient httpClient = new OkHttpClient();

    @PostMapping
    public void handleWebhook(@RequestBody Map<String, Object> payload) {
        String ticker = (String) payload.get("ticker");
        double price = Double.parseDouble(payload.get("price").toString());
        String alert = (String) payload.get("alert");

        String message = "Alert: " + alert + " on " + ticker + " at price " + price;
        sendToTelegram(message);
        sendToBybit("Buy", price, 1, price - 100, price + 200);
    }

    private void sendToTelegram(String message) {
        String url = "https://api.telegram.org/bot" + telegramBotToken + "/sendMessage?chat_id=" + telegramChatId + "&text=" + message;
        Request request = new Request.Builder().url(url).build();

        try (Response response = httpClient.newCall(request).execute()) {
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendToBybit(String side, double price, int qty, double stopLoss, double takeProfit) {
        String url = "https://api.bybit.com/v2/private/order/create";

        Map<String, Object> params = Map.of(
                "api_key", bybitApiKey,
                "side", side,
                "symbol", "BTCUSD",
                "order_type", "Limit",
                "qty", qty,
                "price", price,
                "time_in_force", "GoodTillCancel",
                "stop_loss", stopLoss,
                "take_profit", takeProfit,
                "timestamp", System.currentTimeMillis()
        );

        String sign = BybitSignatureUtils.createSignature(bybitApiSecret, params);
        params.put("sign", sign);

        FormBody.Builder formBuilder = new FormBody.Builder();
        params.forEach((key, value) -> formBuilder.add(key, value.toString()));

        okhttp3.RequestBody formBody = formBuilder.build();
        Request request = new Request.Builder().url(url).post(formBody).build();

        try (Response response = httpClient.newCall(request).execute()) {
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}