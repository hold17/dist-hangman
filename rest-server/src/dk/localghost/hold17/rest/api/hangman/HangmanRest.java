package dk.localghost.hold17.rest.api.hangman;

import com.google.gson.Gson;
import com.sun.xml.internal.ws.fault.ServerSOAPFaultException;
import dk.localghost.authwrapper.transport.AuthenticationException;
import dk.localghost.hold17.dto.HangmanGame;
import dk.localghost.hold17.dto.HighScore;
import dk.localghost.hold17.dto.Token;
import dk.localghost.hold17.helpers.*;
import dk.localghost.hold17.rest.auth.AuthenticationEndpoint;
import dk.localghost.hold17.transport.IHangman;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.ws.WebServiceException;
import java.util.List;

@SuppressWarnings("Duplicates")

@Path("hangman")
public class HangmanRest {
    private static final Gson gson = new Gson();

    /**
     * Creates a new Hangman instance. If a game already exist, it will be destroyed first.
     * @param servletRequest web request
     * @return A new hangman game
     */
    @AuthenticationEndpoint.Auth
    @POST
    @Path("game")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewGame(@Context HttpServletRequest servletRequest) {
        final String header = servletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        final String tokenStr = header.substring("Bearer ".length()).trim();

        Token token = new Token();
        token.setAccess_token(tokenStr);

        final IHangman hangman;

        try {
            hangman = HangmanHelper.createNewGame(token);
        } catch (FatalServerException e) {
            ErrorObj err = new ErrorObj();
            err.setError_type("internal_server_error");
            err.setError_message(e.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(gson.toJson(err)).build();
        } catch (WebServiceException e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(gson.toJson(ErrorBuilder.buildServiceUnavailable())).build();
        }

        HangmanGame currentGame = new HangmanGame(hangman);

        return Response
                .status(Response.Status.CREATED)
                .entity(gson.toJson(currentGame))
                .build();
    }

    /**
     * Get an active game for the specified user
     * @param servletRequest web request
     * @return current state of the game, or bad request if it does not exist
     */
    @AuthenticationEndpoint.Auth
    @GET
    @Path("game")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGame(@Context HttpServletRequest servletRequest) {
        final String header = servletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        final String tokenStr = header.substring("Bearer ".length()).trim();

        Token token = new Token();
        token.setAccess_token(tokenStr);

        final IHangman hangman;

        try {
            hangman = HangmanHelper.getHangmanService(token);
        } catch (FatalServerException e) {
            ErrorObj err = new ErrorObj();
            err.setError_type("internal_server_error");
            err.setError_message(e.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(gson.toJson(err)).build();
        } catch (WebServiceException e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(gson.toJson(ErrorBuilder.buildServiceUnavailable())).build();
        }

        if (hangman == null) {
            ErrorObj err = new ErrorObj("not_found", "No game exist for this user yet.");
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(err)).build();
        }

        HangmanGame currentGame = new HangmanGame(hangman);

        return Response
                .status(Response.Status.OK)
                .entity(gson.toJson(currentGame))
                .build();
    }

    /**
     * Start a game if one exists
     * @param servletRequest web request
     * @return current state of the game
     */
    @AuthenticationEndpoint.Auth
    @PUT
    @Path("game")
    @Produces(MediaType.APPLICATION_JSON)
    public Response startGame(@Context HttpServletRequest servletRequest) {
        final String header = servletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        final String tokenStr = header.substring("Bearer ".length()).trim();

        Token token = new Token();
        token.setAccess_token(tokenStr);

        token = AuthorizationHelper.getAuthService().extractToken(token);

        final IHangman hangman;

        try {
            hangman = HangmanHelper.getHangmanService(token);
        } catch(FatalServerException e) {
            ErrorObj err = new ErrorObj();
            err.setError_type("internal_server_error");
            err.setError_message(e.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(gson.toJson(err)).build();
        } catch (WebServiceException e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(gson.toJson(ErrorBuilder.buildServiceUnavailable())).build();
        }

        if (hangman == null) {
            ErrorObj err = new ErrorObj();
            err.setError_type("not_found");
            err.setError_message("A game was not created for that user.");
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(err)).build();
        }

        try {
            hangman.startNewGame(token);
        } catch (AuthenticationException e) {
            ErrorObj err = new ErrorObj("authentication_exception", e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(err).build();
        } catch (WebServiceException e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(gson.toJson(ErrorBuilder.buildServiceUnavailable())).build();
        }

        HangmanGame game = new HangmanGame(hangman);

        return Response.accepted().entity(gson.toJson(game)).build();
    }

    /**
     * Guess a letter if a game exist
     * @param servletRequest
     * @param letter
     * @return current state of the game
     */
    @AuthenticationEndpoint.Auth
    @POST
    @Path("guess/{letter}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response guessLetter(@Context HttpServletRequest servletRequest, @PathParam("letter") String letter) {
        final String header = servletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        final String tokenStr = header.substring("Bearer ".length()).trim();

        Token token = new Token();
        token.setAccess_token(tokenStr);
        token = AuthorizationHelper.getAuthService().extractToken(token);

        final IHangman hangman;

        try {
            hangman = HangmanHelper.getHangmanService(token);
        } catch(FatalServerException e) {
            ErrorObj err = new ErrorObj();
            err.setError_type("internal_server_error");
            err.setError_message(e.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(gson.toJson(err)).build();
        } catch (WebServiceException e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(gson.toJson(ErrorBuilder.buildServiceUnavailable())).build();
        }

        if (hangman == null) {
            ErrorObj err = new ErrorObj();
            err.setError_type("not_found");
            err.setError_message("A game was not created for that user.");
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(err)).build();
        }

        if (!hangman.hasGameBegun()) {
            ErrorObj err = new ErrorObj();
            err.setError_type("game_not_started");
            err.setError_message("The game has not started yet. Send a PUT request to /api/hangman/game");
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(err)).build();
        }

        try {
            hangman.guess(letter, token);
        } catch (AuthenticationException e) {
            ErrorObj err = new ErrorObj("authentication_exception", e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(err).build();
        } catch (ServerSOAPFaultException e) {
            ErrorObj err = new ErrorObj();
            err.setError_type("serversoapfault_exception");
            err.setError_message("Game finished but could not execute createHighScore() in HangmanLogic.java.");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(gson.toJson(err)).build();
        } catch (WebServiceException e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(gson.toJson(ErrorBuilder.buildServiceUnavailable())).build();
        }
        HangmanGame currentGame = new HangmanGame(hangman);
        return Response.status(Response.Status.ACCEPTED).entity(gson.toJson(currentGame)).build();

    }

    @AuthenticationEndpoint.Auth
    @DELETE
    @Path("game")
    public Response destroyGame(@Context HttpServletRequest servletRequest) {
        final String header = servletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        final String tokenStr = header.substring("Bearer ".length()).trim();

        Token token = new Token();
        token.setAccess_token(tokenStr);

        try {
            HangmanHelper.destroyHangmanService(token);
        } catch(FatalServerException e) {
            ErrorObj err = new ErrorObj();
            err.setError_type("internal_server_error");
            err.setError_message(e.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(gson.toJson(err)).build();
        } catch (WebServiceException e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(gson.toJson(ErrorBuilder.buildServiceUnavailable())).build();
        }

        return Response.ok().build();
    }

    @GET
    @Path("highscores")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHighScoreList() {
        final List<HighScore> hsList;

        try {
            hsList = DatabaseHelper.CreateNewHandler().getHighScoreList();
        } catch (FatalServerException e) {
            ErrorObj err = new ErrorObj();
            err.setError_type("internal_server_error");
            err.setError_message(e.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(gson.toJson(err)).build();
        } catch (WebServiceException e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(gson.toJson(ErrorBuilder.buildServiceUnavailable())).build();
        }

        return Response.accepted().entity(gson.toJson(hsList)).build();
    }

}