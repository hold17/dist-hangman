package dk.localghost.hold17.server;

import dk.localghost.authwrapper.dto.User;
import dk.localghost.authwrapper.transport.AuthenticationException;
import dk.localghost.authwrapper.transport.ConnectivityException;
import dk.localghost.hold17.dto.Token;
import dk.localghost.hold17.helpers.TokenHelper;
import dk.localghost.hold17.transport.IAuthentication;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebService(endpointInterface = "dk.localghost.hold17.transport.IAuthentication")
public class Authentication implements IAuthentication {
    private HashMap<String, Endpoint> hangmanServices;
    private List<String> userPlaying = new ArrayList<>();
    private static Endpoint dbHandlerEndpoint;

    public Authentication() {
        hangmanServices = new HashMap<>();
        CleanUpThread cleanUpThread = new CleanUpThread();
        new Thread(cleanUpThread).start();
        // create DatabaseHandler
        if (dbHandlerEndpoint == null)
            dbHandlerEndpoint = Endpoint.publish(getDatabaseHandlerServiceURL(), new DatabaseHandler());
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
        userPlaying.add(userName);

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
        userPlaying.remove(userName);
        System.out.println(userName + " just ended his game.");
        hangmanServices.get(userName).stop();
        hangmanServices.remove(userName);
    }

    public String getDatabaseHandlerServiceURL() {
        return "http://" + Server.ADDRESS + ":" + Server.PORT + "/" + Server.ADDRESS_HANGMAN + "/highscores";
    }

    private class CleanUpThread implements Runnable {
        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        private void cleanUp() {
            try {
                System.out.println("Scheduled cleanUp started");
                for (String s : userPlaying) {
                    long time = ((HangmanLogic) hangmanServices.get(s).getImplementor()).getInstanceLastActiveTime();
                    time = System.currentTimeMillis() - time;
                    if (time > 60000) {
                        hangmanServices.get(s).stop();
                        hangmanServices.remove(s);
                        System.out.println(s + ": Game Stopped");
                        userPlaying.remove(s);
                    }
                }
            } catch (Exception e) {
                System.out.println("ScheduleAtFixedRate failed");
            }
        }

        public void run() {
            System.out.println("CleanUpThread Started");
            executor.scheduleAtFixedRate(this::cleanUp, 0, 20L, TimeUnit.SECONDS);
        }
    }
}
