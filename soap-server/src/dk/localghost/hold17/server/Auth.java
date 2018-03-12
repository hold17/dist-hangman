package dk.localghost.hold17.server;

import dk.localghost.authwrapper.dto.Speed;
import dk.localghost.authwrapper.dto.User;
import dk.localghost.authwrapper.helper.UserAdministrationFactory;
import dk.localghost.authwrapper.transport.IUserAdministration;
import dk.localghost.authwrapper.transport.AuthenticationException;
import dk.localghost.authwrapper.transport.ConnectivityException;

public class Auth {

    private static IUserAdministration getUserAdministration() throws ConnectivityException{
        return UserAdministrationFactory.getUserAdministration(Speed.LUDICROUS_SPEED); // Ludicrous = rmi, slow = soap
    }

    public static User signIn(String username, String password) throws AuthenticationException {
        try {
            IUserAdministration ua = getUserAdministration();
            return ua.authenticateUser(username, password);
        } catch (ConnectivityException e) {
            System.out.println("Failed to contact server. " + e.getMessage());
        }

        return null;
    }

    public static void forgotPassword(String username) throws ConnectivityException{
        System.out.println("Sending forgotten password e-mail...");

        try {
            IUserAdministration ua = getUserAdministration();
            ua.sendForgottenPasswordEmail(username, "You clearly forgot your password, stupid.");
        } catch (ConnectivityException e) {
            throw new ConnectivityException("Failed to contact server. " + e.getMessage());
        }

        System.out.println("E-mail has been successfully sent.");
    }
}
