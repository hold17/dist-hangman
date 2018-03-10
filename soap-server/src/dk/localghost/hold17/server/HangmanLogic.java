package dk.localghost.hold17.server;

import dk.localghost.authwrapper.dto.User;
import dk.localghost.authwrapper.dto.Speed;
import dk.localghost.authwrapper.transport.AuthenticationException;
import dk.localghost.authwrapper.helper.UserAdministrationFactory;
import dk.localghost.authwrapper.transport.ConnectivityException;
import dk.localghost.hold17.transport.IHangman;

import javax.jws.WebService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

@WebService(endpointInterface = "dk.localghost.hold17.transport.IHangman")
public class HangmanLogic implements IHangman {
    private ArrayList<String> possibleWords = new ArrayList<>();
    private String word;
    private ArrayList<String> usedLetters = new ArrayList<>();
    private String visibleWord;
    private int wrongLettersCount;
    private boolean lastGuessedLetterIsCorrect;
    private boolean gameHasBeenWon;
    private boolean gameHasBeenLost;

    public  String getWord() {
        return this.word;
    }
    public String getVisibleWord() {
        return this.visibleWord;
    }
    public ArrayList<String> getPossibleWords() {
        return this.possibleWords;
    }
    public ArrayList<String> getUsedLetters() {
        return this.usedLetters;
    }
    public String getUsedLettersStr() {
        StringBuilder usedLetters = new StringBuilder();
        for (String l : this.usedLetters) {
            usedLetters.append(l);
        }
        return usedLetters.toString();
    }
    public int getWrongLettersCount() {
        return this.wrongLettersCount;
    }
    public boolean isLastLetterCorrect() {
        return this.lastGuessedLetterIsCorrect;
    }
    public boolean isGameWon() {
        return this.gameHasBeenWon;
    }
    public boolean isGameLost() {
        return this.gameHasBeenLost;
    }
    public boolean isGameOver() { return this.gameHasBeenLost || this.gameHasBeenWon; }

    public HangmanLogic() {
        addDemoData();
        reset(); // This is the only place reset() without a user may be called...
    }

    public HangmanLogic(HangmanLogic hangman) {
        this.possibleWords = hangman.getPossibleWords();
        this.word = hangman.getWord();
        this.usedLetters = hangman.getUsedLetters();
        this.visibleWord = hangman.getVisibleWord();
        this.wrongLettersCount = hangman.getWrongLettersCount();
        this.gameHasBeenWon = hangman.isGameWon();
        this.gameHasBeenLost = hangman.isGameLost();
    }

    private void addDemoData() {
        possibleWords.clear();

        possibleWords.add("car");
        possibleWords.add("computer");
        possibleWords.add("programming");
        possibleWords.add("highway");
        possibleWords.add("route");
        possibleWords.add("walkway");
        possibleWords.add("snail");
        possibleWords.add("bird");
    }

    public void reset(User user) throws AuthenticationException {
        try { authenticateUser(user); } catch (ConnectivityException e) { return; }
        reset();
        System.out.println(user.getUsername() + " started a new game. The new word is " + this.word);
    }

    private void reset() {
        usedLetters.clear();
        wrongLettersCount = 0;
        gameHasBeenWon = false;
        gameHasBeenLost = false;
        word = possibleWords.get(new Random().nextInt(possibleWords.size()));
        updateVisibleWord();
    }

    private void updateVisibleWord() {
        visibleWord = "";
        gameHasBeenWon = true;
        for (int n = 0; n < word.length(); n++) {
            String letter = word.substring(n, n + 1);
            if (usedLetters.contains(letter)) {
                visibleWord += letter;
            } else {
                visibleWord += "*";
                gameHasBeenWon = false;
            }
        }
    }

    public void guess(String givenLetter, User user) throws AuthenticationException {
        try { authenticateUser(user); } catch (ConnectivityException e) { return; }

        final String letter = givenLetter.toLowerCase();
//        System.out.println("Guess: " + letter);

        if (letter.length() != 1) return;
//        System.out.println("User guess: " + letter);
        if(usedLetters.contains(letter)) return;
        if (gameHasBeenWon || gameHasBeenLost) return;

        usedLetters.add(letter);

        if (word.contains(letter)) {
            lastGuessedLetterIsCorrect = true;
//            System.out.println("Letter was correct: " + letter);
        } else {
            // Vi gættede på et bogstav der ikke var i ordet.
            lastGuessedLetterIsCorrect = false;
//            System.out.println("Letter was NOT correct:" + letter);
            wrongLettersCount = wrongLettersCount + 1;
            if (wrongLettersCount > 6) {
                gameHasBeenLost = true;
            }
        }
        updateVisibleWord();
    }

    public void logStatus() {
        System.out.println("---------- ");
        System.out.println("- word (hidden) = " + word);
        System.out.println("- visibleWord = " + visibleWord);
        System.out.println("- wrongLettersCount = " + wrongLettersCount);
        System.out.println("- usedLetters = " + usedLetters);
        if (gameHasBeenLost) System.out.println("- GAME LOST");
        if (gameHasBeenWon) {System.out.println("- GAME WON"); System.out.println(" - Score = " + this.calculateScore()); }

        System.out.println("---------- ");
    }

    public static String getUrl(String url) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();
        while (line != null) {
            sb.append(line).append("\n");
            line = br.readLine();
        }
        return sb.toString();
    }

    public void getWordsFromWeb(String url, User user) throws IOException, AuthenticationException {
        String data = getUrl(url);

        data = data.substring(data.indexOf("<body")). // fjern headere
                replaceAll("<.+?>", " ").toLowerCase(). // fjern tags
                replaceAll("&#198;", "æ"). // erstat HTML-tegn
                replaceAll("&#230;", "æ"). // erstat HTML-tegn
                replaceAll("&#216;", "ø"). // erstat HTML-tegn
                replaceAll("&#248;", "ø"). // erstat HTML-tegn
                replaceAll("&oslash;", "ø"). // erstat HTML-tegn
                replaceAll("&#229;", "å"). // erstat HTML-tegn
                replaceAll("[^a-zæøå]", " "). // fjern tegn der ikke er bogstaver
                replaceAll(" [a-zæøå] "," "). // fjern 1-bogstavsord
                replaceAll(" [a-zæøå][a-zæøå] "," "); // fjern 2-bogstavsord

        data = data.trim();

        System.out.println("data = " + data);
        System.out.println("data = " + Arrays.asList(data.split("\\s+")));
        possibleWords.clear();
        possibleWords.addAll(new HashSet<String>(Arrays.asList(data.split(" "))));

        System.out.println("muligeOrd = " + possibleWords);
        reset(user);
    }

    public double calculateScore() {
        if (!this.isGameOver()) return -1.0;

        final int lengthOfWord = this.word.length();
        final int uniqueLetterCount = this.uniqueLettersOfWord().length();
        final int wrongGuessCount = this.wrongLettersCount;

        final double score = (lengthOfWord + uniqueLetterCount) / (wrongGuessCount + 1);

        return score;
    }

    public String uniqueLettersOfWord() {
        StringBuilder uniqueLetters = new StringBuilder();

        for (int i = 0; i < this.word.length(); i++) {
            char current = this.word.charAt(i);
            if (uniqueLetters.toString().indexOf(current) < 0)
                uniqueLetters.append(current);
            else
                uniqueLetters = new StringBuilder(uniqueLetters.toString().replace(String.valueOf(current), ""));
        }

        return uniqueLetters.toString();
    }

    private static void authenticateUser(User user) throws AuthenticationException, ConnectivityException {
        try {
            UserAdministrationFactory.getUserAdministration(Speed.SLOW).authenticateUser(user.getUsername(), user.getPassword());
        } catch (ConnectivityException e) {
            throw new ConnectivityException("Failed to contact server. " + e.getMessage(), e.getCause());
        }
    }
}
