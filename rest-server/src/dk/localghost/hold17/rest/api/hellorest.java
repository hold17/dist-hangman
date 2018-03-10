package dk.localghost.hold17.rest.api;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import dk.localghost.authwrapper.dto.Speed;
import dk.localghost.authwrapper.dto.User;
import dk.localghost.authwrapper.helper.UserAdministrationFactory;
import dk.localghost.authwrapper.transport.AuthenticationException;
import dk.localghost.authwrapper.transport.ConnectivityException;
import dk.localghost.authwrapper.transport.IUserAdministration;
import com.google.gson.Gson;
import dk.localghost.hold17.helpers.AuthorizationHelper;
import dk.localghost.hold17.rest.auth.AuthenticationEndpoint;

import java.net.MalformedURLException;

@Path("hello")
public class hellorest {
    private final Gson gson = new Gson();

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response test() {
        return Response.ok("Hello, World!").build();
    }

    @POST
    @Path("authorize")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticate(@FormParam("username") String username, @FormParam("password") String password) {
        final IUserAdministration userAdmin;
        final User user;

        try {
            userAdmin = UserAdministrationFactory.getUserAdministration(Speed.LUDICROUS_SPEED);
            user = userAdmin.authenticateUser(username, password);
        } catch (ConnectivityException e) {
            ErrorObj err = new ErrorObj();
            err.setError_type("Connectivity ErrorObj");
            err.setError_message(e.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(gson.toJson(err)).build();
        } catch (AuthenticationException e) {
            ErrorObj err = new ErrorObj();
            err.setError_type("Authentication ErrorObj");
            err.setError_message(e.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(gson.toJson(err)).build();
        }

        return Response.ok().entity(gson.toJson(user)).build();
    }

    @GET
    @AuthenticationEndpoint.Auth
    @Path("secure")
    @Produces(MediaType.TEXT_PLAIN)
    public Response secureResource(@Context HttpServletRequest servletRequest) {
        final String header = servletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        final String token = header.substring("Bearer ".length()).trim();

        try {
            final User user = AuthorizationHelper.getAuthService().getUserFromToken(token);

            return Response.ok("Hello " + user.getFirstname() + ". This is a secured resource.").build();
        } catch (MalformedURLException e) {
            ErrorObj err = new ErrorObj("internal_error", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(gson.toJson(err)).build();
        }
    }
}
