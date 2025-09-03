package ru.tishembitov.authservice.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tishembitov.authservice.dto.UserDto;
import ru.tishembitov.authservice.dto.UserHeader;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateAccessToken(UserDto user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getAccessTokenExpiration());

        return Jwts.builder()
                .setSubject(user.username())
                .claim("userId", user.id())
                .claim("email", user.email())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .setIssuer(jwtProperties.getIssuer())
                .setAudience(jwtProperties.getAudience())
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Optional<UserHeader> validateAndExtractUser(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Проверка срока действия
            if (claims.getExpiration().before(new Date())) {
                log.warn("Token expired for user: {}", claims.getSubject());
                return Optional.empty();
            }

            Long userId = claims.get("userId", Long.class);
            String username = claims.getSubject();

            return Optional.of(new UserHeader(userId, username));

        } catch (ExpiredJwtException e) {
            log.error("JWT expired: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error validating JWT: {}", e.getMessage());
        }

        return Optional.empty();
    }
}