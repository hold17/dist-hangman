package dk.localghost.hold17.client;

import dk.localghost.authwrapper.dto.User;
import dk.localghost.authwrapper.transport.AuthenticationException;
import dk.localghost.authwrapper.transport.ConnectivityException;
import dk.localghost.hold17.dto.Token;
import dk.localghost.hold17.ui.GallowDrawer;
import dk.localghost.hold17.transport.IAuthentication;
import dk.localghost.hold17.transport.IHangman;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;

public class HangmanClient {
    private IAuthentication auth;
    private IHangman hangman;
    private Token token;

    private String address;
    private int port;

    HangmanClient(String address, int port) throws AuthenticationException, MalformedURLException {
        this.address = address;
        this.port = port;

        auth = connectToAuthenticationService(address, port);

        token = authenticateUser();
        System.out.println("Welcome, " + token.getUser().getFirstname() + " " + token.getUser().getLastname());

        auth.createHangmanService(token);
        hangman = connectToHangmanService(auth, token);
    }

    private static IAuthentication connectToAuthenticationService(String address, int port) throws MalformedURLException {
        final String SERVICE = "auth";
        final URL url = new URL( "http://" + address + ":" + port + "/" + SERVICE + "?wsdl");

        QName qname = new QName("http://server.hold17.localghost.dk/", "AuthenticationService");

        Service ser = Service.create(url, qname);

        return ser.getPort(IAuthentication.class);
    }

    private static IHangman connectToHangmanService(IAuthentication auth, Token token) throws MalformedURLException {
        final URL hangUrl = new URL( auth.getHangmanServiceURL(token) + "?wsdl");

        QName hangQname = new QName("http://server.hold17.localghost.dk/", "HangmanLogicService");
        Service hangSvc = Service.create(hangUrl, hangQname);

        return hangSvc.getPort(IHangman.class);
    }

    private Token authenticateUser() throws AuthenticationException{
        String username = null, password = null;
        Token token;

        for (int retries = 3; retries > 0; retries--) {
            username = UserInteraction.getString("Username");
            password = UserInteraction.getString("Password");

            User user = new User();
            user.setUsername(username);
            user.setPassword(password);

            try {
                System.out.println("Authenticating...");
                token = auth.authorize(user);
            } catch (AuthenticationException e) {
                System.out.println("Wrong username or password.");
                System.out.println(retries - 1 + " retries left.\n");
                continue;
            }

            return token;
        }

        System.out.println("Sending e-mail...");

        try {
            auth.forgotPassword(username);
        } catch (ConnectivityException e) {
            System.err.println(e.getMessage());
        }
        
        System.out.println("E-mail sent.");

        throw new AuthenticationException("Failed to authorize");
    }

    public void startGame() throws AuthenticationException {
        if (hangman == null) throw new NullPointerException("Connection to server was unsuccessful, hangman object is still null.");

        if (!hangman.hasGameBegun())
            hangman.startNewGame(token);

        System.out.println("Game has started. The word has " + hangman.getVisibleWord().length() + " letters.\n");

        while(!hangman.isGameOver()) {
            displayStatus();

            String letterToGuess = UserInteraction.getLetter();

            // Cheat
            if (letterToGuess.equals("isuckathangman")) {
                System.out.println("word: " + hangman.getWord());
                continue;
            }

            try {
                hangman.guess(letterToGuess, token);

            } catch (AuthenticationException e) {
                System.err.println(e.getMessage());
                token = authenticateUser();
            }
        } // end loop
        displayStatus();

        hangman.logStatus();

        System.out.println("Game was " + (hangman.isGameWon() ? "won" : "lost") + ". \n\n");
        System.out.println("Your final score was " + hangman.getCurrentScore() + " with a time of " + hangman.getFormattedTime());
        if (!UserInteraction.getString("Press 'enter' to start a new game or 'q' and 'enter' to exit").toLowerCase().equals("q")) {
            if (hangman.isGameLost())
                hangman.resetScoreAndTime(token);
            startGame();
        } else {
            auth.endGame(token);
        }
    }

    private void displayStatus() {
            System.out.println("Word to guess: " + hangman.getVisibleWord() + "\n");
            GallowDrawer.drawGallow(hangman.getWrongLettersCount());
            System.out.println("Guessed letters: " + hangman.getUsedLettersStr());
    }

}