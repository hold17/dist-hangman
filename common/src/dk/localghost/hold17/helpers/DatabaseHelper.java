package dk.localghost.hold17.helpers;

import dk.localghost.hold17.transport.IAuthentication;
import dk.localghost.hold17.transport.IDatabaseHandler;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import java.net.MalformedURLException;
import java.net.URL;

public class DatabaseHelper {
    private final static String namespaceURI = "http://server.hold17.localghost.dk/";
    private final static String localPart = "DatabaseHandlerService";

    public static IDatabaseHandler CreateNewHandler() throws FatalServerException {
        final IAuthentication auth = AuthorizationHelper.getAuthService();

        if (auth == null) throw new FatalServerException("IAuthentication is null.");

        URL databaseUrl = null;

        try {
            databaseUrl = new URL(auth.getDatabaseHandlerServiceURL() + "?wsdl");
        } catch (MalformedURLException e) {
            throw new FatalServerException("Some URL was malformed: " + e.getMessage());
        }

        QName hangmanQname = new QName(namespaceURI, localPart);
        Service hangmanService = Service.create(databaseUrl, hangmanQname);

        return hangmanService.getPort(IDatabaseHandler.class);
    }

}