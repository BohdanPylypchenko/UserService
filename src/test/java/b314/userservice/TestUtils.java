package b314.userservice;

import b314.userservice.entity.user.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class TestUtils {

    public static String mapToJson(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }

    public static Object jsonToClass(String json, Class c) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, c);
    }

    public static String createJwt(String secret, int hsLifetime, User user) {
        // Calculating expiration date
        Date expiryDate = Date.from((LocalDateTime.now()
                              .plusHours(hsLifetime))
                              .atZone(ZoneId.systemDefault())
                              .toInstant());

        // Creating jwt
        String jwt =  Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();

        // Returning with bearer
        return ("Bearer " + jwt);
    }

}
