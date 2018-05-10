package dk.localghost.hold17.rest.auth;

import dk.localghost.hold17.dto.Token;
import dk.localghost.hold17.helpers.AuthorizationHelper;

import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@AuthenticationEndpoint.Auth
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    @Context
    private ResourceInfo resourceInfo;

    /**
     * Validates a specific token given as an authorization header
     * @param containerRequestContext
     */
    @Override
    public void filter(ContainerRequestContext containerRequestContext) {
        // Get http header from the request
        final String authorizationHeader = containerRequestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Check if the auth header exists and is correctly formatted
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException("Authorization header must be provided");
        }

        final String accessToken = authorizationHeader.substring("Bearer ".length()).trim();

        Token token = new Token();
        token.setAccess_token(accessToken);

        // See if the token is valid
        // Because returning null is somehow better than just throwing an exception
        // we have to check for a NPE
        try {
            if (!AuthorizationHelper.getAuthService().validateToken(token)) {
                containerRequestContext.abortWith(
                        Response.status(Response.Status.UNAUTHORIZED).build()
                );
            }
        } catch (NullPointerException e) {
            containerRequestContext.abortWith(
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR).build()
            );
            e.printStackTrace();
        }
    }

}