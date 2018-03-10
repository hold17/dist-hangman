package dk.localghost.hold17.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.localghost.authwrapper.dto.User;
import dk.localghost.hold17.dto.Token;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.crypto.MacProvider;

import java.security.Key;
import java.util.Date;

public class TokenHelper {
    public final static Key AUTH_KEY = MacProvider.generateKey(SignatureAlgorithm.HS512);
    public final static String AUTH_TOKEN_TYPE = "Bearer";
    public final static String AUTH_ISSUER = "hold17.localghost.dk";
    public final static String AUTH_CLAIM_USER = "user";

    public static Token issueToken(User user) {
        final Date today = new Date();
        final Date expiration = new Date(today.getTime() + (1000 * 60 * 60 * 1)); // One hour expiration

        user.setPassword(null);

        String jwtToken = Jwts.builder()
                .setIssuedAt(today)
                .setIssuer(AUTH_ISSUER)
                .claim(AUTH_CLAIM_USER, user)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS512, AUTH_KEY)
                .compact();

        Token token = new Token();
        token.setAccess_token(jwtToken);
        token.setToken_type(AUTH_TOKEN_TYPE);
        token.setExpires_in(expiration.getTime());
        token.setUser(user);

        return token;
    }

    public static User getUserFromToken(String accessToken) {
        final Claims claims = Jwts.parser().setSigningKey(AUTH_KEY).parseClaimsJws(accessToken).getBody();
        final ObjectMapper objm = new ObjectMapper();

        return objm.convertValue(claims.get(AUTH_CLAIM_USER), User.class);
    }

    public static boolean isTokenValid(String token) {
        try {
            Jwts.parser().setSigningKey(AUTH_KEY).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            return false;
        }

        return true;
    }
}
