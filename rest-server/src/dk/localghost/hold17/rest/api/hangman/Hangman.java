package dk.localghost.hold17.rest.api.hangman;

import com.google.gson.Gson;
import dk.localghost.hold17.dto.HangmanGame;
import dk.localghost.hold17.dto.Token;
import dk.localghost.hold17.helpers.HangmanHelper;
import dk.localghost.hold17.rest.auth.AuthenticationEndpoint;
import dk.localghost.hold17.transport.IHangman;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("hangman")
public class Hangman {
    private static final Gson gson = new Gson();

    @AuthenticationEndpoint.Auth
    @POST
    @Path("game")
    @Produces(MediaType.APPLICATION_JSON)
    public Response startGame(@Context HttpServletRequest servletRequest) {
        final String header = servletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        final String tokenStr = header.substring("Bearer ".length()).trim();

        Token token = new Token();
        token.setAccess_token(tokenStr);

        IHangman hangman = HangmanHelper.getHangmanService(token);

        HangmanGame game = new HangmanGame();
        game.setVisibleWord(hangman.getVisibleWord());
        System.out.println(hangman.getWord());

        return Response.status(Response.Status.CREATED).entity(gson.toJson(game)).build();
    }
}
