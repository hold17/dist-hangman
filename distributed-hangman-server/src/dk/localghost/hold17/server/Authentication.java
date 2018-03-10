package dk.localghost.hold17.server;

import authwrapper.dto.User;
import authwrapper.transport.AuthenticationException;
import authwrapper.transport.ConnectivityException;
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
    public User authenticate(User user) throws AuthenticationException {
        try {
            User validUser = Auth.signIn(user.getUsername(), user.getPassword());

            HangmanLogic hangman = new HangmanLogic();
            if (hangmanServices.get(user.getUsername()) == null) {
                Endpoint endpoint = Endpoint.publish(getHangmanService(validUser), hangman);
                System.out.println(user.getUsername() + " created a new game. Word is: " + hangman.getWord());
                hangmanServices.put(user.getUsername(), endpoint);
            } else {
                System.out.println(user.getUsername() + " rejoined a game.");
            }

            return validUser;
        } catch (AuthenticationException e) {
            throw new AuthenticationException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public String getHangmanService(User user) {
        return "http://" + Server.ADDRESS + ":" + Server.PORT + "/" + Server.ADDRESS_HANGMAN + "/" + user.getUsername();
    }

    @Override
    public void forgotPassword(String userName) throws ConnectivityException {
        Auth.forgotPassword(userName);
    }

    @Override
    public void endGame(User user) throws AuthenticationException {
        user = Auth.signIn(user.getUsername(), user.getPassword());

        if (hangmanServices.get(user.getUsername()) == null) return;

        System.out.println(user.getUsername() + " just ended his game.");
        hangmanServices.get(user.getUsername()).stop();
        hangmanServices.remove(user.getUsername());
    }
}
