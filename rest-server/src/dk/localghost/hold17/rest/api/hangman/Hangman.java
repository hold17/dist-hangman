package dk.localghost.hold17.rest.api.hangman;

import com.google.gson.Gson;
import dk.localghost.authwrapper.dto.User;
import dk.localghost.hold17.dto.HangmanGame;
import dk.localghost.hold17.helpers.AuthorizationHelper;
import dk.localghost.hold17.rest.auth.AuthenticationEndpoint;
import dk.localghost.hold17.transport.IAuthentication;
import dk.localghost.hold17.transport.IHangman;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;

@Path("hangman")
public class Hangman {

    private static final Gson gson = new Gson();
    private HangmanGame game = new HangmanGame();
    private IAuthentication auth;
    private IHangman hangman;
    private User user;

    private String address = "127.0.0.1";
    private int port = 1337;

//    @AuthenticationEndpoint.Auth
    @GET
    @Path("game")
    @Produces(MediaType.APPLICATION_JSON)
//    public Response startGame(@Context HttpServletRequest servletRequest) {
    public Response startGame() {
        final String authSERVICE = "auth";
        user = new User("s165242", "mismismis");
        try {
//            final String header = servletRequest.getHeader(HttpHeaders.AUTHORIZATION);
//            final String token = header.substring("Bearer ".length()).trim();
//            user = AuthorizationHelper.getAuthService().getUserFromToken(token);

            final URL authURL = new URL("http://" + address + ":" + port + "/" + authSERVICE + "?wsdl");
            QName qname = new QName("http://server.hold17.localghost.dk/", "AuthenticationService");
            Service ser = Service.create(authURL, qname);
            auth = ser.getPort(IAuthentication.class);

            final URL hangUrl = new URL( auth.getHangmanService(user) + "?wsdl");
            QName hangQname = new QName("http://server.hold17.localghost.dk/", "HangmanLogicService");
            Service hangSer = Service.create(hangUrl, hangQname);
            hangman = hangSer.getPort(IHangman.class);
            game.setVisibleWord(hangman.getVisibleWord());
        } catch(MalformedURLException ex) {

        }
        System.out.println(hangman.getWord());
        return Response.ok(gson.toJson(game)).build();
    }
}
