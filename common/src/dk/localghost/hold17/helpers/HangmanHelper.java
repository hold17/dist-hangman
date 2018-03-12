package dk.localghost.hold17.helpers;

import com.sun.istack.internal.Nullable;
import dk.localghost.authwrapper.dto.User;
import dk.localghost.hold17.dto.HangmanGame;
import dk.localghost.hold17.dto.Token;
import dk.localghost.hold17.transport.IAuthentication;
import dk.localghost.hold17.transport.IHangman;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;

public class HangmanHelper {
    public static IHangman getHangmanService(Token token, Boolean get) {
        IAuthentication auth = null;
        URL hangUrl = null;
        try {
            auth = AuthorizationHelper.getAuthService();
            token.setUser(auth.getUserFromToken(token));

            hangUrl = new URL( auth.getHangmanServiceURL(token) + "?wsdl");
        } catch(MalformedURLException ex) {
            // TODO: Remove after Sebastian's commit
        }

        if(!auth.isGameCreated(token)) {
            if(get) { return null; }
            auth.createHangmanService(token);
        }

        QName hangQname = new QName("http://server.hold17.localghost.dk/", "HangmanLogicService");
        Service hangSer = Service.create(hangUrl, hangQname);

        IHangman hangman = hangSer.getPort(IHangman.class);

        return hangman;
    }
}
