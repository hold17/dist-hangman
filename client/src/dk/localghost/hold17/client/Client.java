package dk.localghost.hold17.client;

import dk.localghost.authwrapper.transport.AuthenticationException;

import java.net.MalformedURLException;

public class Client {
    private static String ADDRESS = "localhost";
    private static int PORT = 1337;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java hangman_client.jar <ip/fqdn> <port>");
            System.exit(1);
        }

        ADDRESS = args[0];

        try {
            PORT = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println(args[1] + " is not a number.");
            System.exit(1);
        }

        System.out.println("Will connect to " + ADDRESS + " on port " + PORT + ".");

        HangmanClient hangman = null;

        try {
            hangman = new HangmanClient(ADDRESS, PORT);
        } catch (MalformedURLException e) {
            System.err.println("Failed to connect to server. Make sure it is online, and the address is correct. " + e.getMessage());
        } catch (AuthenticationException e) {
            System.err.println(e.getMessage());
        }

        if (hangman == null) return;

        try {
            hangman.startGame();
        } catch (AuthenticationException e) {
            System.err.println(e.getMessage() + " Please log in again.");
        }
    }

}