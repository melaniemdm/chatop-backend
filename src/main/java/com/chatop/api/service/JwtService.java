package com.chatop.api.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;

    private static final String HEADER = Base64.getUrlEncoder().encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes());

    public String getUsernameFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            Map<String, Object> payloadMap = parsePayload(payload);
            return (String) payloadMap.get("sub");
        } catch (Exception e) {
            return null;
        }
    }
    public String generateToken(String username) {
        long expirationTimeMillis = 1000 * 60 * 60 * 10; // 10 heures de validité
        long expirationDate = System.currentTimeMillis() + expirationTimeMillis;

        // Création de la charge utile (payload)
        Map<String, Object> payload = new HashMap<>();
        payload.put("sub", username);
        payload.put("exp", expirationDate);

        String payloadBase64 = Base64.getUrlEncoder().encodeToString(payload.toString().getBytes());

        // Concaténation du header et du payload
        String headerPayload = HEADER + "." + payloadBase64;

        // Génération de la signature
        String signature = sign(headerPayload);

        // Construction du JWT final
        return headerPayload + "." + signature;
    }

    private String sign(String data) {
        try {
            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmacSha256.init(secretKeySpec);
            return Base64.getUrlEncoder().encodeToString(hmacSha256.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la signature du token JWT", e);
        }
    }

    public boolean validateToken(String token, String username) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;

            String headerPayload = parts[0] + "." + parts[1];
            String signature = parts[2];

            // Vérification de la signature
            String expectedSignature = sign(headerPayload);

            if (!expectedSignature.equals(signature)) return false;

            // Décodage et vérification du payload
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));

            Map<String, Object> payload = parsePayload(payloadJson);

            String tokenUsername = (String) payload.get("sub");

            String expiration = (String) payload.get("exp");

            boolean testName = username.equals(tokenUsername);

            boolean testExpirationDate = Long.valueOf(expiration) >= System.currentTimeMillis();

            return testExpirationDate && testName;
        } catch (Exception e) {
            return false;
        }
    }

    private Map<String, Object> parsePayload(String payloadJson) {
        Map<String, Object> payload = new HashMap<>();
        payloadJson = payloadJson.replace("{", "").replace("}", "");
        String[] pairs = payloadJson.split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            payload.put(keyValue[0].trim(), keyValue[1].trim());
        }
        return payload;
    }
}
