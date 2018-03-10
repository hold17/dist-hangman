package dk.localghost.hold17.transport;

import dk.localghost.authwrapper.dto.User;
//import authwrapper.transport.SomethingWentWrongException;

import javax.jws.WebService;
import dk.localghost.authwrapper.transport.AuthenticationException;
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
//    private void addDemoData();
    void reset(User user) throws AuthenticationException;
//    private void updateVisibleWord();
    void guess(String givenLetter, User user) throws AuthenticationException;
    void logStatus();

//    public static String getUrl(String url) throws IOException {
//        BufferedReader br = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
//        StringBuilder sb = new StringBuilder();
//        String line = br.readLine();
//        while (line != null) {
//            sb.append(line + "\n");
//            line = br.readLine();
//        }
//        return sb.toString();
//    }

    void getWordsFromWeb(String url, User user) throws IOException, AuthenticationException;
    double calculateScore();
    String uniqueLettersOfWord();
}
