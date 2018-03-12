package dk.localghost.hold17.rest.auth;

import com.google.gson.Gson;
import dk.localghost.hold17.dto.Token;
import dk.localghost.hold17.helpers.AuthorizationHelper;
import dk.localghost.hold17.rest.api.ErrorObj;
import dk.localghost.hold17.rest.config.Routes;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(Routes.OAUTH_VALIDATE)
public class Validation {
    private final Gson gson = new Gson();

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response validate(@Context HttpServletRequest servletRequest) {
        final String header = servletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        final String accessToken = header.substring("Bearer ".length()).trim();
        Token token = new Token();
        token.setAccess_token(accessToken);

        if (AuthorizationHelper.getAuthService().validateToken(token)) {
            return Response.ok(gson.toJson(AuthorizationHelper.getAuthService().extractToken(token))).build();
        } else {
            ErrorObj err = new ErrorObj();

            err.setError_type("invalid_token");
            err.setError_message("The token is invalid. It might be expired or maybe changed in an invalid way.");

            return Response.status(Response.Status.UNAUTHORIZED).entity(gson.toJson(err)).build();
        }
    }
}
