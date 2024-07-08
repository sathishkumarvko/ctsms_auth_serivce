package com.cts.authenticationservice.utils;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
	private static final String SECRET_KEY = "SECRET_KEY"; //@TODO Add Signature here
    private static final String ISSUER = "com.ctsms";
    private static final String JWT_HEADER = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
    private JSONObject payload = new JSONObject();
    private String signature;
    private String encodedHeader;
    
    private JwtUtil() {
        encodedHeader = encode(new JSONObject(JWT_HEADER));
    }
   
    public JwtUtil(JSONObject payload) {
        this(payload.getString("email"), payload.getString("password"), payload.getLong("exp"));
    }
    public JwtUtil(String email, String password, long expires) {
        this();
        payload.put("email", email);
        payload.put("password", password);
        payload.put("exp", expires);
        payload.put("iat", LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        payload.put("iss", ISSUER);
        payload.put("jti", UUID.randomUUID().toString());
        signature = hmacSha256(encodedHeader + "." + encode(payload), SECRET_KEY);
    }
    /**
     * For verification
     *
     * @param token
     * @throws java.security.NoSuchAlgorithmException
     */
    public JwtUtil(String token) throws NoSuchAlgorithmException {
        this();
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid Token format");
        }
        if (encodedHeader.equals(parts[0])) {
            encodedHeader = parts[0];
        } else {
            throw new NoSuchAlgorithmException("JWT Header is Incorrect: " + parts[0]);
        }
        payload = new JSONObject(decode(parts[1]));
        if (payload.isEmpty()) {
            throw new JSONException("Payload is Empty: ");
        }
        if (!payload.has("exp")) {
            throw new JSONException("Payload doesn't contain expiry " + payload);
        }
        signature = parts[2];
    }
    @Override
    public String toString() {
        return encodedHeader + "." + encode(payload) + "." + signature;
    }
    public boolean isValid() {
        return payload.getLong("exp") > (LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) //token not expired
                && signature.equals(hmacSha256(encodedHeader + "." + encode(payload), SECRET_KEY)); //signature matched
    }
    public String getEmail() {
        return payload.getString("email");
    }
    public String getPassword() {
        return payload.getString("password");
    }
    private static String encode(JSONObject obj) {
        return encode(obj.toString().getBytes(StandardCharsets.UTF_8));
    }
    private static String encode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    private static String decode(String encodedString) {
        return new String(Base64.getUrlDecoder().decode(encodedString));
    }
    /**
     * Sign with HMAC SHA256 (HS256)
     *
     * @param data
     * @return
     * @throws Exception
     */
    private String hmacSha256(String data, String secret) {
        try {
            //MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = secret.getBytes(StandardCharsets.UTF_8);//digest.digest(secret.getBytes(StandardCharsets.UTF_8));
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(hash, "HmacSHA256");
            sha256Hmac.init(secretKey);
            byte[] signedBytes = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return encode(signedBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            Logger.getLogger(JwtUtil.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        }
    }
}
