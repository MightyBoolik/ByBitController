package com.example.demo;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

public class BybitSignatureUtils {

    public static String createSignature(String secret, Map<String, Object> params) {
        try {
            String queryString = params.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue().toString())
                    .sorted()
                    .collect(Collectors.joining("&"));

            Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmacSHA256.init(secretKeySpec);
            byte[] hash = hmacSHA256.doFinal(queryString.getBytes(StandardCharsets.UTF_8));
            return Hex.encodeHexString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate signature", e);
        }
    }
}

