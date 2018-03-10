package dk.localghost.hold17.transport;

import dk.localghost.authwrapper.dto.User;
import dk.localghost.authwrapper.transport.AuthenticationException;
import dk.localghost.authwrapper.transport.ConnectivityException;
import dk.localghost.hold17.dto.Token;

import javax.jws.WebService;

@WebService
public interface IAuthentication {
    Token authorize(User user) throws AuthenticationException;
    String getHangmanService(User user);
    void forgotPassword(String userName) throws ConnectivityException;
    void endGame(User user) throws AuthenticationException;
}