package dk.localghost.hold17.transport;

import dk.localghost.authwrapper.transport.AuthenticationException;
import dk.localghost.hold17.dto.Token;

import javax.jws.WebService;

import java.util.List;

@WebService
public interface IHangman {
    String getWord();
    String getWordDefinition();
    String getWordExampleBefore();
    String getWordExampleAfter();
    String getVisibleWord();
    List<String> getWordSynonyms();
    List<String> getUsedLetters();
    String getUsedLettersStr();
    String getUniqueLettersOfWord();
    String getFormattedTime();

    int getGameType();
    int getWrongLettersCount();
    int getCurrentScore();
    long getCurrentSessionTime();
    long getInstanceLastActiveTime();
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
    void findWordFromServerFile(String url);
}