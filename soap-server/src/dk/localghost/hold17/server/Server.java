package dk.localghost.hold17.server;

import dk.localghost.hold17.transport.IAuthentication;
import javax.xml.ws.Endpoint;
import java.nio.file.Paths;

public class Server {
    public static int PORT = 1099;
    public static String ADDRESS = "[::]";
    public static final String ADDRESS_AUTH = "auth";
    public static final String ADDRESS_HANGMAN = "hangman";

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java hangman_server.jar <ip/fqdn> <port>");
            System.exit(1);
        }

        if (!args[0].toLowerCase().equals("localhost")) {
            ADDRESS = args[0];
        }

        try {
            PORT = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println(args[1] + " is not a number.");
            System.exit(1);
        }

        IAuthentication auth = new Authentication();
        Endpoint.publish("http://" + ADDRESS + ":" + PORT + "/" + ADDRESS_AUTH, auth);

        // print the path on which we are looking for the dbsettings.properties as paths in java are confusing
        final String currentPath = Paths.get("").toAbsolutePath().toString() + "\\dbsettings.properties";
        final String propertiesPath = currentPath.replace("//", "/");

        System.out.println("Looking for " + propertiesPath);

        System.out.println("Server started on " + ADDRESS + " port " + PORT);
    }

}