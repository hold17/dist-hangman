package dk.localghost.hold17.rest.auth;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import dk.localghost.authwrapper.dto.Speed;
import dk.localghost.authwrapper.dto.User;
import dk.localghost.authwrapper.helper.UserAdministrationFactory;
import dk.localghost.authwrapper.transport.AuthenticationException;
import dk.localghost.authwrapper.transport.ConnectivityException;
import dk.localghost.authwrapper.transport.IUserAdministration;
import dk.localghost.hold17.dto.Token;
import dk.localghost.hold17.rest.api.ErrorObj;
import dk.localghost.hold17.rest.config.AuthConfig;
import dk.localghost.hold17.rest.config.Routes;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

//@Path(Routes.OAUTH_ROOT)
@Path(Routes.OAUTH_AUTHORIZE)
public class AuthenticationEndpoint {
    private static final Gson gson = new Gson();

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response authorize(@FormParam("username") String username, @FormParam("password") String password) {
        final IUserAdministration userAdmin;
        final User user;

        try {
            userAdmin = UserAdministrationFactory.getUserAdministration(Speed.LUDICROUS_SPEED);
            user = userAdmin.authenticateUser(username, password);
        } catch (ConnectivityException e) {
            ErrorObj err = new ErrorObj();

            err.setError_type("connectivity_error");
            err.setError_message(e.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(gson.toJson(err)).build();
        } catch (AuthenticationException e) {
            ErrorObj err = new ErrorObj();

            err.setError_type("connectivity_error");
            err.setError_message(e.getMessage());

            return Response.status(Response.Status.UNAUTHORIZED).entity(gson.toJson(err)).build();
        }

        return Response.ok().entity(gson.toJson(issueToken(user))).build();
    }

    private static Token issueToken(User user) {
        final Date today = new Date();
        final Date expiration = new Date(today.getTime() + (1000 * 60 * 60 * 1)); // One hour expiration

        user.setPassword(null);

        String jwtToken = Jwts.builder()
                .setIssuedAt(today)
                .setIssuer(AuthConfig.AUTH_ISSUER)
                .claim(AuthConfig.AUTH_CLAIM_USER, user)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS512, AuthConfig.AUTH_KEY)
                .compact();

        Token token = new Token();
        token.setAccess_token(jwtToken);
        token.setToken_type(AuthConfig.AUTH_TOKEN_TYPE);
        token.setExpires_in(expiration.getTime());
        token.setUser(user);

        return token;
    }
}
