package dk.localghost.hold17.rest.auth;

import dk.localghost.authwrapper.dto.User;
import dk.localghost.hold17.dto.Token;
import dk.localghost.hold17.helpers.AuthorizationHelper;
import dk.localghost.hold17.helpers.TokenHelper;
import dk.localghost.hold17.transport.IAuthentication;

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
import java.io.IOException;
import java.net.MalformedURLException;

@AuthenticationEndpoint.Auth
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        // Get http header from the request
        final String authorizationHeader = containerRequestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Check if the auth header exists and is correctly formatted
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException("Authorization header must be provided");
        }

        final String token = authorizationHeader.substring("Bearer ".length()).trim();

        try {
            // See if the token is valid
            if (!AuthorizationHelper.getAuthService().validateToken(token))
                containerRequestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED).build()
                );
        } catch (MalformedURLException e) {
            containerRequestContext.abortWith(
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build()
            );
        }
    }
}
