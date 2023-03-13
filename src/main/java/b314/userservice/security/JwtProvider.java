package b314.userservice.security;

import b314.userservice.entity.user.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Implements jwt related functionality
 */
@Component
public class JwtProvider {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInHs}")
    private int jwtExpirationInHours;

    /**
     * Generates JWT token from given authentication instance
     * @param authentication Instance to generate from
     * @return JWT as String
     */
    public String generateToken(Authentication authentication) {
        // Getting user details
        CustomUserDetails userPrincipal = (CustomUserDetails) authentication.getPrincipal();

        // Calculating expiration date
        Date expiryDate = Date.from((LocalDateTime.now()
                                                  .plusHours(jwtExpirationInHours))
                                                  .atZone(ZoneId.systemDefault())
                                                  .toInstant());

        // Returning jwt
        return Jwts.builder()
                   .setSubject(userPrincipal.getUsername())
                   .setIssuedAt(new Date())
                   .setExpiration(expiryDate)
                   .signWith(SignatureAlgorithm.HS512, jwtSecret)
                   .compact();
    }

    /**
     * Parses given token to get username
     * @param token to parse
     * @return username from token
     */
    public String getUserUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                            .setSigningKey(jwtSecret)
                            .parseClaimsJws(token)
                            .getBody();
        return claims.getSubject();
    }

    /**
     * Validates given token
     * @param authToken token to validate
     * @return is token valid?
     */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(authToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
