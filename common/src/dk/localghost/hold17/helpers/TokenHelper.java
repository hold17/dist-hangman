package dk.localghost.hold17.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.localghost.authwrapper.dto.User;
import dk.localghost.hold17.dto.Token;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.crypto.MacProvider;

import java.security.Key;
import java.util.Date;

public class TokenHelper {
    private final static Key AUTH_KEY = MacProvider.generateKey(SignatureAlgorithm.HS512);
    private final static String AUTH_TOKEN_TYPE = "Bearer";
    private final static String AUTH_ISSUER = "hold17.localghost.dk";
    private final static String AUTH_CLAIM_USER = "user";

    public static Token issueToken(User user) {
        final Date today = new Date();
        final Date expiration = new Date(today.getTime() + (1000 * 60 * 60)); // One hour expiration

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

    // TODO: change argument from Token token to String tokenStr
    public static Token extractToken(Token token) {
        final Claims claims = Jwts.parser().setSigningKey(AUTH_KEY).parseClaimsJws(token.getAccess_token()).getBody();
        final ObjectMapper objm = new ObjectMapper();

        token.setToken_type(AUTH_TOKEN_TYPE);
        token.setExpires_in(claims.getExpiration().getTime());
        token.setUser(objm.convertValue(claims.get(AUTH_CLAIM_USER), User.class));

        return token;
    }

    public static boolean isTokenValid(Token token) {
        try {
            Jwts.parser().setSigningKey(AUTH_KEY).parseClaimsJws(token.getAccess_token()).getBody();
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            return false;
        }

        return true;
    }
}
