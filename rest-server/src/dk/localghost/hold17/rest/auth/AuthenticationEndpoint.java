package dk.localghost.hold17.rest.auth;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import com.google.gson.Gson;
import dk.localghost.authwrapper.dto.User;
import dk.localghost.authwrapper.transport.AuthenticationException;
import dk.localghost.hold17.dto.Token;
import dk.localghost.hold17.rest.api.ErrorObj;
import dk.localghost.hold17.rest.config.Routes;
import dk.localghost.hold17.transport.IAuthentication;

import java.net.MalformedURLException;
import java.net.URL;

@Path(Routes.OAUTH_AUTHORIZE)
public class AuthenticationEndpoint {
    private static final Gson gson = new Gson();

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response authorize2(@FormParam("username") String username, @FormParam("password") String password) {
        IAuthentication auth;

        final String ADDRESS = "localhost";
        final int PORT = 1337;
        final String SERVICE = "auth";
        final URL url;

        try {
            url = new URL( "http://" + ADDRESS + ":" + PORT + "/" + SERVICE + "?wsdl");

            QName qname = new QName("http://server.hold17.localghost.dk/", "AuthenticationService");
            Service service = Service.create(url, qname);

            auth = service.getPort(IAuthentication.class);

            Token token = auth.authorize(new User(username, password));

            return Response.ok().entity(gson.toJson(token)).build();
        } catch (MalformedURLException e) {
            ErrorObj err = new ErrorObj();

            err.setError_type("malformed_url");
            err.setError_message(e.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(gson.toJson(err)).build();
        } catch (AuthenticationException e) {
            ErrorObj err = new ErrorObj();

            err.setError_type("connectivity_error");
            err.setError_message(e.getMessage());

            return Response.status(Response.Status.UNAUTHORIZED).entity(gson.toJson(err)).build();
        }
    }
}
