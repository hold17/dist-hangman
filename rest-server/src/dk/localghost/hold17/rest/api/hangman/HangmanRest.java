package dk.localghost.hold17.rest.api.hangman;

import com.google.gson.Gson;
import dk.localghost.authwrapper.transport.AuthenticationException;
import dk.localghost.hold17.dto.HangmanGame;
import dk.localghost.hold17.dto.Token;
import dk.localghost.hold17.helpers.HangmanHelper;
import dk.localghost.hold17.helpers.TokenHelper;
import dk.localghost.hold17.rest.api.ErrorObj;
import dk.localghost.hold17.rest.auth.AuthenticationEndpoint;
import dk.localghost.hold17.transport.IHangman;

import javax.lang.model.type.ErrorType;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("hangman")
public class HangmanRest {
        private static final Gson gson = new Gson();

    //Start new game
    @AuthenticationEndpoint.Auth
    @POST
    @Path("game")
    @Produces(MediaType.TEXT_XML)
    public Response startGame(@Context HttpServletRequest servletRequest) {
        final String header = servletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        final String tokenStr = header.substring("Bearer ".length()).trim();

        Token token = new Token();
        token.setAccess_token(tokenStr);

        IHangman hangman = HangmanHelper.getHangmanService(token, false);
        try {
            hangman.startNewGame(token);
        } catch(AuthenticationException ex) {
            ErrorObj error = new ErrorObj("Authentication error: " + ex.getMessage());
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(gson.toJson(error))
                    .build();
        }
        HangmanGame currentGame = new HangmanGame();
        currentGame.setGame(hangman);
        return Response
                .status(Response.Status.CREATED).entity(gson.toJson(currentGame))
                .build();
    }

    //Get game
    @AuthenticationEndpoint.Auth
    @GET
    @Path("game")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGame(@Context HttpServletRequest servletRequest) {
        final String header = servletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        final String tokenStr = header.substring("Bearer ".length()).trim();

        Token token = new Token();
        token.setAccess_token(tokenStr);

        IHangman hangman = HangmanHelper.getHangmanService(token, true);
        if(hangman == null) {
            ErrorObj error = new ErrorObj("A game has not yet been created.");
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(gson.toJson(error))
                    .build();
        }
        HangmanGame currentGame = new HangmanGame();
        currentGame.setGame(hangman);

        return Response
                .status(Response.Status.ACCEPTED)
                .entity(gson.toJson(currentGame))
                .build();
    }

    //Guess letter
    @AuthenticationEndpoint.Auth
    @POST
    @Path("guess/{letter}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response guessLetter(@Context HttpServletRequest servletRequest, @PathParam("letter") String letter) {
        final String header = servletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        final String tokenStr = header.substring("Bearer ".length()).trim();

        Token token = new Token();
        token.setAccess_token(tokenStr);

        IHangman hangman = HangmanHelper.getHangmanService(token, false);

        try {
            hangman.guess(letter, token);
        } catch(AuthenticationException ex) {
            ErrorObj error = new ErrorObj("Authentication error: " + ex.getMessage());
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(gson.toJson(error))
                    .build();
        }

        HangmanGame currentGame = new HangmanGame();
        currentGame.setGame(hangman);

        return Response
                .status(Response.Status.CREATED)
                .entity(gson.toJson(currentGame))
                .build();
    }
}