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
    private int correctlyGuessedLettersCount;
    private boolean lastGuessedLetterIsCorrect;
    private boolean gameHasBeenWon;
    private boolean gameHasBeenLost;
    private long timeStart, timeStop;

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

    // getters
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
    public int getWrongLettersCount() {
        return this.wrongLettersCount;
    }

    // gamestate checks
    public boolean isLastLetterCorrect() {
        return this.lastGuessedLetterIsCorrect;
    }
    public boolean isGameWon() {
        return this.gameHasBeenWon;
    }
    public boolean isGameLost() {
        return this.gameHasBeenLost;
    }
    public boolean isGameOver() {
        return this.gameHasBeenLost || this.gameHasBeenWon;
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
        correctlyGuessedLettersCount = 0;
        gameHasBeenWon = false;
        gameHasBeenLost = false;
        word = possibleWords.get(new Random().nextInt(possibleWords.size()));
        updateVisibleWord();
        timeStart = System.currentTimeMillis();
    }

    private void updateVisibleWord() {
        StringBuilder sb = new StringBuilder();
        String letter;
        correctlyGuessedLettersCount = 0;
        for (int n = 0; n < word.length(); n++) {
            letter = word.substring(n, n + 1);
            if (usedLetters.contains(letter)) {
                sb.append(letter);
                correctlyGuessedLettersCount++;
            } else {
                sb.append("*");
            }
        }
        visibleWord = sb.toString();
    }

    public void guess(String givenLetter, User user) throws AuthenticationException {
        try { authenticateUser(user); } catch (ConnectivityException e) { return; }

        final String letter = givenLetter.toLowerCase();

        // guard against all kinds of badness
        if (letter.length() != 1) return;
        else if (usedLetters.contains(letter)) return;
        else if (gameHasBeenWon || gameHasBeenLost) return;

        usedLetters.add(letter);

        updateVisibleWord();

        if (word.contains(letter)) {
            lastGuessedLetterIsCorrect = true;
            if (word.length() == correctlyGuessedLettersCount) {
                gameHasBeenWon = true;
            }
        } else {
            lastGuessedLetterIsCorrect = false;
            wrongLettersCount++;
            if (wrongLettersCount > 6) {
                gameHasBeenLost = true;
            }
        }
    }

    public void logStatus() {
        System.out.println("----------");
        System.out.println("- word (hidden) = " + word);
        System.out.println("- visibleWord = " + visibleWord);
        System.out.println("- wrongLettersCount = " + wrongLettersCount);
        System.out.println("- usedLetters = " + usedLetters);
        if (gameHasBeenLost) System.out.println("- GAME LOST");
        if (gameHasBeenWon) {System.out.println("- GAME WON"); System.out.println(" - Score = " + this.calculateScore() + " || " + this.calcScore()); }

        System.out.println("----------");
    }

    private static String getUrl(String url) throws IOException {
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

        data = data.substring(data.indexOf("<body")). // remove headers
                replaceAll("<.+?>", " ").toLowerCase(). // remove tags
                replaceAll("&#198;", "æ"). // replace HTML-symbols
                replaceAll("&#230;", "æ"). // replace HTML-symbols
                replaceAll("&#216;", "ø"). // replace HTML-symbols
                replaceAll("&#248;", "ø"). // replace HTML-symbols
                replaceAll("&oslash;", "ø"). // replace HTML-symbols
                replaceAll("&#229;", "å"). // replace HTML-symbols
                replaceAll("[^a-zæøå]", " "). // remove symbols that aren't letters
                replaceAll("[a-zæøå]"," "). // remove 1-letter words
                replaceAll("[a-zæøå][a-zæøå]"," "); // remove 2-letter words

        data = data.trim();

        System.out.println("data = " + data);
        System.out.println("data = " + Arrays.asList(data.split("\\s+")));
        possibleWords.clear();
        possibleWords.addAll(new HashSet<String>(Arrays.asList(data.split(" "))));

        System.out.println("possibleWords = " + possibleWords);
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

    private int calcScore() {
        long timeInMillis = timeStop - timeStart;

        final int lengthOfWord = this.word.length();
        final int uniqueLetterCount = this.uniqueLettersOfWord().length();
        final int wrongGuessCount = this.wrongLettersCount;

        System.out.println("time: " + Math.round(timeInMillis/1000));
        // I'm tired so this score calc sucks...
        int score = (int) Math.round(Math.round(timeInMillis/100) * Math.pow(0.9, (lengthOfWord - wrongGuessCount) + uniqueLetterCount));
        System.out.println("score: " + score);

        return score;
    }

    public String getUsedLettersStr() {
        StringBuilder sb = new StringBuilder();
        for (String l : this.usedLetters) {
            sb.append(l);
        }
        return sb.toString();
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
