package dk.localghost.hold17.rest.auth;

import dk.localghost.authwrapper.dto.User;
import dk.localghost.hold17.dto.Token;
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
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

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
            if (!isTokenValid(token)) containerRequestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED).build()
            );
        } catch (MalformedURLException e) {
            containerRequestContext.abortWith(
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build()
            );
        }
    }

    private static boolean isTokenValid(String token) throws MalformedURLException {
        IAuthentication auth;

        final String ADDRESS = "localhost";
        final int PORT = 1337;
        final String SERVICE = "auth";
        final URL url;
        final String URL_STR = "http://" + ADDRESS + ":" + PORT + "/" + SERVICE + "?wsdl";

        try {
            url = new URL(URL_STR);

            QName qname = new QName("http://server.hold17.localghost.dk/", "AuthenticationService");
            Service service = Service.create(url, qname);
            auth = service.getPort(IAuthentication.class);
        } catch (MalformedURLException e) {
            throw new MalformedURLException("The url specified is invalid: " + URL_STR);
        }

        return auth.validateToken(token);
    }
}
