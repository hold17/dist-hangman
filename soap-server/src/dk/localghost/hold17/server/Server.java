package dk.localghost.hold17.server;

import dk.localghost.hold17.transport.IAuthentication;
import javax.xml.ws.Endpoint;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Server {
    public static int PORT = 1099;
    public static String ADDRESS = "[::]";
    public static final String ADDRESS_AUTH = "auth";
    public static final String ADDRESS_HANGMAN = "hangman";
    public static Properties properties;

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

        loadDatabaseSettingsFromFile();

        IAuthentication auth = new Authentication();
        Endpoint.publish("http://" + ADDRESS + ":" + PORT + "/" + ADDRESS_AUTH, auth);
        System.out.println("Server started on " + ADDRESS + " port " + PORT);
    }

    private static void loadDatabaseSettingsFromFile() {
        Properties props = new Properties();

        String currentPath = System.getProperty("user.dir");

        try {
            FileInputStream fs = new FileInputStream(currentPath + "/dbsettings.properties");
            props.load(fs);
        } catch (FileNotFoundException e) {
            System.err.println("dbsettings could not be found at path: " + currentPath + "/");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("File read error");
            e.printStackTrace();
        }

        //InputStream is = ClassLoader.getSystemResourceAsStream("dbsettings.properties");

//        if (is == null) {
//            System.err.println("OH NOES!");
//            return;
//        }
//        try {
//            props.load(fs);
//        } catch (IOException e) {
//            System.err.println("dbsettings could not be found");
//            e.printStackTrace();
//        }
        properties = props;
    }
}
