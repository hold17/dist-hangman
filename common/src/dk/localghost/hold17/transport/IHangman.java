package dk.localghost.hold17.transport;

import javax.jws.WebService;
import dk.localghost.authwrapper.transport.AuthenticationException;
import dk.localghost.hold17.dto.HighScore;
import dk.localghost.hold17.dto.Token;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebService
public interface IHangman {
    String getWord();
    String getVisibleWord();
    ArrayList<String> getPossibleWords();
    ArrayList<String> getUsedLetters();
    String getUsedLettersStr();
    String getUniqueLettersOfWord();
    int getWrongLettersCount();
    int getCurrentScore();
    long getCurrentTime();
    boolean isLastLetterCorrect();
    boolean isGameWon();
    boolean isGameLost();
    boolean isGameOver();
    boolean hasGameBegun();
    void startNewGame(Token token) throws AuthenticationException;
    void reset(Token token) throws AuthenticationException;
    void resetScoreAndTime(Token token) throws AuthenticationException;
    void guess(String givenLetter, Token token) throws AuthenticationException;
    void logStatus();
    void getWordsFromWeb(String url, Token token) throws IOException, AuthenticationException;
    String getFormattedTime();
    List<HighScore> getHighScoreList(/*Token token*/) /*throws AuthenticationException*/;
}