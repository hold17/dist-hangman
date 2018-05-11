package dk.localghost.hold17.helpers;

import dk.localghost.hold17.dto.Token;
import dk.localghost.hold17.transport.IAuthentication;
import dk.localghost.hold17.transport.IHangman;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import java.net.MalformedURLException;
import java.net.URL;

public class HangmanHelper {
    private final static String namespaceURI = "http://server.hold17.localghost.dk/";
    private final static String localPart = "HangmanLogicService";
    /**
     * Create a new game. Destroy existing if it exists
     * @param token valid access token
     * @return A new hangman instance
     */
    public static IHangman createNewGame(Token token) throws FatalServerException {
        final IAuthentication auth = AuthorizationHelper.getAuthService();

        if (auth == null) throw new FatalServerException("IAuthentication is null.");

        token = auth.extractToken(token);
        URL hangmanUrl = null;

        // If a game already exist, end it
        if(auth.isGameCreated(token))
            auth.endGame(token);

        auth.createHangmanService(token);

        // Get the url of the soap wsdl
        try {
            hangmanUrl = new URL(auth.getHangmanServiceURL(token) + "?wsdl");
        } catch (MalformedURLException e) {
            throw new FatalServerException("Some url was malformed: " + e.getMessage());
        }

        QName hangmanQname = new QName(namespaceURI, localPart);
        Service hangmanService = Service.create(hangmanUrl, hangmanQname);

        return hangmanService.getPort(IHangman.class);
    }

    /**
     * Will check if a game exists and then return it
     * @param token valid access token
     * @return An existing hangman instance or null of no instance exists for the specified user
     */
    public static IHangman getHangmanService(Token token) throws FatalServerException {
        final IAuthentication auth = AuthorizationHelper.getAuthService();

        if (auth == null) throw new FatalServerException("IAuthentication is null.");

        token = auth.extractToken(token);
        URL hangmanUrl = null;

        // If a game exists, return null
        if (!auth.isGameCreated(token)) return null;

        // Get the url of the soap wsdl
        try {
            hangmanUrl = new URL(auth.getHangmanServiceURL(token) + "?wsdl");
        } catch (MalformedURLException e) {
            throw new FatalServerException("Some url was malformed: " + e.getMessage());
        }

        QName hangmanQname = new QName(namespaceURI, localPart);
        Service hangmanService = Service.create(hangmanUrl, hangmanQname);

        return hangmanService.getPort(IHangman.class);
    }

    /**
     * Destory a game for a specific player
     * @param token valid access token
     * @throws FatalServerException
     */
    public static void destroyHangmanService(Token token) throws FatalServerException {
        final IAuthentication auth = AuthorizationHelper.getAuthService();

        if (auth == null) throw new FatalServerException("IAuthentication is null.");

        token = auth.extractToken(token);

        if (auth.isGameCreated(token)) auth.endGame(token);
    }

}