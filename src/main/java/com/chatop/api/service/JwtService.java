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
    // The secret key used for signing the JWT, injected from application properties.
    @Value("${jwt.secret}")
    private String secretKey;
    // The JWT header in Base64 format, specifying the algorithm (HS256) and type (JWT)
    private static final String HEADER = Base64.getUrlEncoder().encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes());

    /**
     * Extracts the username from the JWT token by decoding the payload part.
     *
     * @param token the JWT token.
     * @return the username if valid, or null if an error occurs.
     */
    public String getUsernameFromToken(String token) {
        try {
            // Decode the payload part of the token (middle section of the token)
            String payload = new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]));
            // Extract the "sub" (subject) field from the payload, which contains the username.
            return (String) parsePayload(payload).get("sub");
        } catch (Exception e) {
            return null;// Return null if any decoding or parsing error occurs.
        }
    }

    /**
     * Extracts the user ID from the JWT token.
     *
     * @param token the JWT token.
     * @return the user ID as a string, or null if an error occurs.
     */
    public String getIDFromToken(String token) {
        // Decode the payload part of the token.
        try {
            // Décoder la partie payload du token
            String payload = new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]));
            // Extract the "id" field from the payload.
            return (String) parsePayload(payload).get("id");
        } catch (Exception e) {
            return null; // Return null if an error occurs during extraction.
        }
    }

    /**
     * Generates a JWT token for a user.
     *
     * @param username the username to include in the token.
     * @param userId   the user ID to include in the token.
     * @return the generated JWT token.
     */
    public String generateToken(String username, Long userId) {
        long expirationTimeMillis = 1000 * 60 * 60 * 10; // 10 hours of validity
        long expirationDate = System.currentTimeMillis() + expirationTimeMillis;
        // Create the payload with the subject (username), expiration time, and user ID.
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

    /**
     * Signs the given data using HMAC-SHA256 and the secret key.
     *
     * @param data the data to sign.
     * @return the generated signature as a Base64 string.
     */
    private String sign(String data) {
        try {
            // Initialize the HMAC-SHA256 algorithm with the secret key.
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            // Generate the signature by hashing the data.
            return Base64.getUrlEncoder().encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            // Throw a runtime exception if the signing process fails.
            throw new RuntimeException("Erreur lors de la signature du token JWT", exception);
        }
    }

    /**
     * Validates the JWT token by checking its signature and expiration.
     *
     * @param token    the JWT token.
     * @param username the expected username to match against the token's subject.
     * @return true if the token is valid; false otherwise.
     */
    public boolean validateToken(String token, String username) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;// A valid JWT must have 3 parts: header, payload, signature.

            // Verify the signature by comparing it with the expected signature.
            String expectedSignature = sign(parts[0] + "." + parts[1]);
            if (!expectedSignature.equals(parts[2])) return false;

            // Decode and parse the payload to extract claims.
            Map<String, Object> payload = parsePayload(new String(Base64.getUrlDecoder().decode(parts[1])));
            // Check if the subject (username) matches and if the token has not expired.
            return username.equals(payload.get("sub")) &&
                    Long.parseLong((String) payload.get("exp")) >= System.currentTimeMillis();
        } catch (Exception exception) {
            return false;// Return false if any validation error occurs.
        }
    }

    /**
     * Parses the payload of a JWT token into a map of key-value pairs.
     *
     * @param payloadJson the JSON string representing the payload.
     * @return a map containing the parsed payload data.
     */
    private Map<String, Object> parsePayload(String payloadJson) {
        Map<String, Object> payload = new HashMap<>();
        payloadJson = payloadJson.replace("{", "").replace("}", ""); // Remove curly braces from the JSON string.
        String[] pairs = payloadJson.split(",");// Split the string into key-value pairs.
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");// Split each pair into key and value.
            payload.put(keyValue[0].trim(), keyValue[1].trim());// Add the pair to the map.
        }
        return payload;
    }
}
