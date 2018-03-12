package dk.localghost.hold17.server;

import dk.localghost.authwrapper.dto.User;
import dk.localghost.authwrapper.transport.AuthenticationException;
import dk.localghost.authwrapper.transport.ConnectivityException;
import dk.localghost.hold17.dto.Token;
import dk.localghost.hold17.helpers.TokenHelper;
import dk.localghost.hold17.transport.IAuthentication;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import java.util.HashMap;

@WebService(endpointInterface = "dk.localghost.hold17.transport.IAuthentication")
public class Authentication implements IAuthentication {
    private HashMap<String, Endpoint> hangmanServices;

    public Authentication() {
        hangmanServices = new HashMap<>();
    }

    @Override
    public Token authorize(User user) throws AuthenticationException {
        final User validUser;

        try {
            validUser = Auth.signIn(user.getUsername(), user.getPassword());
        } catch (AuthenticationException e) {
            throw new AuthenticationException(e.getMessage(), e.getCause());
        }

        return TokenHelper.issueToken(validUser);
    }

    @Override
    public void createHangmanService(Token token) {
        String userName = token.getUser().getUsername();

        if (hangmanServices.get(userName) == null) {
            HangmanLogic hangman = new HangmanLogic();
            Endpoint endpoint = Endpoint.publish(getHangmanServiceURL(token), hangman);

            System.out.println(userName + " created a new game.");
            hangmanServices.put(userName, endpoint);
        } else {
            System.out.println(userName + " rejoined a game.");
        }
    }

    @Override
    public boolean isGameCreated(Token token) {
        return hangmanServices.containsKey(token.getUser().getUsername());
    }

    @Override
    public boolean validateToken(Token token) {
        return TokenHelper.isTokenValid(token);
    }

    @Override
    public Token extractToken(Token token) {
        return TokenHelper.extractToken(token);
    }

    @Override
    public String getHangmanServiceURL(Token token) {
        return "http://" + Server.ADDRESS + ":" + Server.PORT + "/" + Server.ADDRESS_HANGMAN + "/" + token.getUser().getUsername();
    }

    @Override
    public void forgotPassword(String userName) throws ConnectivityException {
        Auth.forgotPassword(userName);
    }

    @Override
    public void endGame(Token token) {
        if (!TokenHelper.isTokenValid(token)) return;

        String userName = token.getUser().getUsername();

        System.out.println(userName + " just ended his game.");
        hangmanServices.get(userName).stop();
        hangmanServices.remove(userName);
    }
}
