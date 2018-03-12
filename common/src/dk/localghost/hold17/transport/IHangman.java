package dk.localghost.hold17.transport;

import javax.jws.WebService;
import dk.localghost.authwrapper.transport.AuthenticationException;
import dk.localghost.hold17.dto.Token;

import java.io.IOException;
import java.util.ArrayList;

@WebService
public interface IHangman {
    String getWord();
    String getVisibleWord();
    ArrayList<String> getPossibleWords();
    ArrayList<String> getUsedLetters();
    String getUsedLettersStr();
    int getWrongLettersCount();
    boolean isLastLetterCorrect();
    boolean isGameWon();
    boolean isGameLost();
    boolean isGameOver();
    void reset(Token token) throws AuthenticationException;
    void guess(String givenLetter, Token token) throws AuthenticationException;
    void logStatus();
    void getWordsFromWeb(String url, Token token) throws IOException, AuthenticationException;
    double calculateScore();
    String uniqueLettersOfWord();
}
