package dk.localghost.hold17.rest.auth;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import dk.localghost.authwrapper.dto.User;
import dk.localghost.authwrapper.transport.AuthenticationException;
import dk.localghost.hold17.dto.Token;
import dk.localghost.hold17.helpers.AuthorizationHelper;
import dk.localghost.hold17.rest.api.ErrorObj;
import dk.localghost.hold17.rest.config.Routes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Endpoint for handling authorization
 */
@Path(Routes.OAUTH_AUTHORIZE)
public class AuthenticationEndpoint {
    private static final Gson gson = new Gson();

    /**
     * Contacts SOAP service to generate a token for the user
     * @param username Form url encoded username
     * @param password Form url encoded password
     * @return A token and user object for a valid user or an error message encoded as json
     */
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response authorize(@FormParam("username") String username, @FormParam("password") String password) {
        try {
            Token token = AuthorizationHelper.getAuthService().authorize(new User(username, password));

            return Response.ok().entity(gson.toJson(token)).build();
        } catch (NullPointerException e) {
            e.printStackTrace();
/*            ErrorObj err = new ErrorObj();

            err.setError_type("malformed_url");
            err.setError_message(e.getMessage());*/
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)/*.entity(gson.toJson(err))*/.build();
        } catch (AuthenticationException e) {
            ErrorObj err = new ErrorObj();

            err.setError_type("connectivity_error");
            err.setError_message(e.getMessage());

            return Response.status(Response.Status.UNAUTHORIZED).entity(gson.toJson(err)).build();
        }
    }

    /**
     * Auth annotation. This annotation forces an endpoint to include an authorization header
     */
    @NameBinding
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD})
    public @interface Auth { }
}
