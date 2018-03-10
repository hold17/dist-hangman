package dk.localghost.hold17.server;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

import java.security.Key;

public class AuthConfig {
    public final static Key AUTH_KEY = MacProvider.generateKey(SignatureAlgorithm.HS512);
    public final static String AUTH_TOKEN_TYPE = "Bearer";
    public final static String AUTH_ISSUER = "hold17.localghost.dk";
    public final static String AUTH_CLAIM_USER = "user";
}
