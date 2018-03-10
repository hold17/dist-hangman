package dk.localghost.hold17.client;

import dk.localghost.authwrapper.dto.User;
import dk.localghost.authwrapper.transport.AuthenticationException;
import dk.localghost.authwrapper.transport.ConnectivityException;
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
    private User user;

    private String address;
    private int port;

    HangmanClient(String address, int port) throws AuthenticationException, MalformedURLException {
        this.address = address;
        this.port = port;

        auth = connectToAuthenticationService(address, port);

        user = authenticateUser();
        System.out.println("Welcome, " + user.getFirstname() + " " + user.getLastname());

        hangman = connectToHangmanService(auth, user);
    }

    private static IAuthentication connectToAuthenticationService(String address, int port) throws MalformedURLException {
        final String SERVICE = "auth";
        final URL url = new URL( "http://" + address + ":" + port + "/" + SERVICE + "?wsdl");

        QName qname = new QName("http://server.hold17.localghost.dk/", "AuthenticationService");

        Service ser = Service.create(url, qname);

        return ser.getPort(IAuthentication.class);
    }

    private static IHangman connectToHangmanService(IAuthentication auth, User user) throws AuthenticationException, MalformedURLException {
        final URL hangUrl = new URL( auth.getHangmanService(user) + "?wsdl");

        QName hangQname = new QName("http://server.hold17.localghost.dk/", "HangmanLogicService");
        Service hangSer = Service.create(hangUrl, hangQname);

        return hangSer.getPort(IHangman.class);
    }

    private User authenticateUser() throws AuthenticationException{
        String username = null, password = null;
        User user;

        for (int retries = 3; retries > 0; retries--) {
            username = UserInteraction.getString("Username");
            password = UserInteraction.getString("Password");

            User userCreds = new User();
            userCreds.setUsername(username);
            userCreds.setPassword(password);

            try {
                System.out.println("Authenticating...");
                user = auth.authorize(userCreds);
            } catch (AuthenticationException e) {
                System.out.println("Wrong username or password.");
                System.out.println(retries - 1 + " retries left.\n");
                continue;
            }

            return user;
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

    public void startGame() {
        if (hangman == null) throw new NullPointerException("Connection to server was unsuccessful, hangman object is still null.");
            while(!hangman.isGameOver()) {
                System.out.println("Game has started. The word has " + hangman.getVisibleWord().length() + " letters.");
                System.out.println();
                displayStatus();

                String letterToGuess = UserInteraction.getLetter();

                if (letterToGuess.equals("$")) {
                    System.out.println("word: " + hangman.getWord());
                    continue;
                }

                try {
                    hangman.guess(letterToGuess, user);
                } catch (AuthenticationException e) {
                    System.err.println(e.getMessage());
                }
            }

            System.out.println("Game was " + (hangman.isGameWon() ? "won" : "lost") + ".");
            System.out.println();
            System.out.println();

            try {
                hangman.reset(user);
                auth.endGame(user);
            } catch (AuthenticationException e) {
                System.err.println(e.getMessage());
            }

            if (!UserInteraction.getString("Press 'enter' to start a new game or 'q' and 'enter' to exit").toLowerCase().equals("q")) {
                try {
                    auth.authorize(user);
                    hangman = connectToHangmanService(auth, user);
                } catch (AuthenticationException|MalformedURLException e) {}
                startGame();
            }
    }

    private void displayStatus() {
            System.out.println("Word to guess: " + hangman.getVisibleWord());
            System.out.println();
            GallowDrawer.drawGallow(hangman.getWrongLettersCount());
            System.out.println("Guessed letters: " + hangman.getUsedLettersStr());
    }
}
