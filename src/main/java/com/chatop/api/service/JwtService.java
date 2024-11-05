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

    // Extrait le nom d'utilisateur (username) du token en décodant la partie payload
    public String getUsernameFromToken(String token) {
        try {
            String payload = new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]));
            return (String) parsePayload(payload).get("sub");
        } catch (Exception e) {
            return null;
        }
    }
    public String getIDFromToken(String token) {
        try {
            // Décoder la partie payload du token
            String payload = new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]));

            // Extraire l'ID à partir du payload
            return (String) parsePayload(payload).get("id");
        } catch (Exception e) {
            return null; // Retourner null si une erreur survient
        }
    }

    // Génère un token JWT pour un utilisateur donné
    public String generateToken(String username, Long userId) {
        long expirationTimeMillis = 1000 * 60 * 60 * 10; // 10 hours of validity
        long expirationDate = System.currentTimeMillis() + expirationTimeMillis;

        Map<String, Object> payload = new HashMap<String, Object>() {{
            put("sub", username);
            put("exp", expirationDate);
            put("id", userId);
        }};

        // Encodage du payload en Base64
        String payloadBase64 = Base64.getUrlEncoder().encodeToString(payload.toString().getBytes());

        // Concatenation of header and payload
        String headerPayload = HEADER + "." + payloadBase64;

        // Signature generation
        String signature = sign(headerPayload);

        // Construction du JWT final
        return headerPayload + "." + signature;
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder().encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new RuntimeException("Erreur lors de la signature du token JWT", exception);
        }
    }

    // Valide le token en vérifiant sa signature et son expiration
    public boolean validateToken(String token, String username) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;

            // Signature Verification
            String expectedSignature = sign(parts[0] + "." + parts[1]);
            if (!expectedSignature.equals(parts[2])) return false;

            // Payload decoding and verification
            Map<String, Object> payload = parsePayload(new String(Base64.getUrlDecoder().decode(parts[1])));
            return username.equals(payload.get("sub")) &&
                    Long.parseLong((String) payload.get("exp")) >= System.currentTimeMillis();
        } catch (Exception e) {
            return false;
        }
    }

    // Parse le payload du JWT pour obtenir un map clé/valeur des données
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
