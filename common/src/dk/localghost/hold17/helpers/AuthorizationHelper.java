package dk.localghost.hold17.helpers;

import dk.localghost.hold17.transport.IAuthentication;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;

public class AuthorizationHelper {
    final static String ADDRESS = "localhost";
    final static int PORT = 1337;
    final static String SERVICE = "auth";
    final static String URL_STR = "http://" + ADDRESS + ":" + PORT + "/" + SERVICE + "?wsdl";

    public static IAuthentication getAuthService() {
        final URL url;

        try {
            url = new URL(URL_STR);

            QName qname = new QName("http://server.hold17.localghost.dk/", "AuthenticationService");
            Service service = Service.create(url, qname);
            return service.getPort(IAuthentication.class);
        } catch (MalformedURLException e) {
            System.err.println("The url specified is invalid: " + URL_STR);
            return null;
        }
    }
}
