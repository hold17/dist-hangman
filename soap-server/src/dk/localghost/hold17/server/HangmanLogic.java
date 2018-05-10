package dk.localghost.hold17.server;

import dk.localghost.authwrapper.transport.AuthenticationException;
import dk.localghost.hold17.dto.Token;
import dk.localghost.hold17.helpers.TokenHelper;
import dk.localghost.hold17.server.database.entities.HighScoreEntity;
import dk.localghost.hold17.transport.IHangman;

import javax.jws.WebService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

@WebService(endpointInterface = "dk.localghost.hold17.transport.IHangman")
public class HangmanLogic implements IHangman {
    private static DatabaseHandler dbh = new DatabaseHandler();
    private ArrayList<String> possibleWords = new ArrayList<>();
    private String word;
    private ArrayList<String> usedLetters = new ArrayList<>();
    private String visibleWord;
    private int wrongLettersCount;
    private int correctlyGuessedLettersCount;
    private boolean lastGuessedLetterIsCorrect;
    private boolean gameHasBeenWon;
    private boolean gameHasBeenLost;
    private boolean hasGameBegun;
    private long sessionStartTime, sessionStopTime;
    private long currentSessionTime;
    private long totalTime;
    private long instanceLastActiveTime;
    private int currentScore;
    private int totalScore;

    public HangmanLogic() {
        this.hasGameBegun = false;
        updateInstanceLastActiveTime();
        addDemoData();
        reset();
    }

    // this is never used because the service is always open until manually closed
    // and no save game handling is implemented
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
    public long getTotalTime() {
        return this.totalTime;
    }
    public long getCurrentSessionTime() {
        return this.currentSessionTime;
    }
    public int getTotalScore() {
        return this.totalScore;
    }
    public int getCurrentScore() {
        return this.currentScore;
    }
    public long getInstanceLastActiveTime() {
        return this.instanceLastActiveTime;
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
    public boolean hasGameBegun() {
        return this.hasGameBegun;
    }

    private void updateTotalTime() {
        this.totalTime += this.currentSessionTime;
    }
    private void updateCurrentSessionTime() {
        this.currentSessionTime = sessionStopTime - sessionStartTime;
    }
    private void updateTotalScore() {
        this.totalScore += this.currentScore;
    }
    private void updateCurrentScore() {
        this.currentScore = calculateScore(this.currentSessionTime);
    }
    private void updateInstanceLastActiveTime() {
        this.instanceLastActiveTime = System.currentTimeMillis();
    }

    public void startNewGame(Token token) throws AuthenticationException {
        reset(token);
        word = possibleWords.get(new Random().nextInt(possibleWords.size()));
        updateVisibleWord();
        this.hasGameBegun = true;
        System.out.println(token.getUser().getUsername() + " started a new game. The new word is " + this.word);
        sessionStartTime = System.currentTimeMillis();
    }

    public void reset(Token token) throws AuthenticationException {
        authenticateUserToken(token);
        reset();
    }

    private void reset() {
        usedLetters.clear();
        wrongLettersCount = 0;
        correctlyGuessedLettersCount = 0;
        gameHasBeenWon = false;
        gameHasBeenLost = false;
    }

    public void resetScoreAndTime(Token token) throws AuthenticationException {
        authenticateUserToken(token);
        this.sessionStopTime = 0;
        this.sessionStartTime = 0;
        this.totalTime = 0;
        this.currentSessionTime = 0;
        this.totalScore = 0;
        this.currentScore = 0;
        System.out.println(token.getUser().getUsername() + " reset their score after a lost game");
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

    public void guess(String givenLetter, Token token) throws AuthenticationException {
        authenticateUserToken(token);
        updateInstanceLastActiveTime();

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
            if (wrongLettersCount >= 6) {
                gameHasBeenLost = true;
            }
        }

        if (isGameOver()) {
            sessionStopTime = System.currentTimeMillis();
            updateCurrentSessionTime();
            updateCurrentScore();
            // currently all high scores are saved by default, should eventually be a user choice
            createHighScore(token, this.currentScore, this.getFormattedTime(), true);
            this.hasGameBegun = false;
        }
    }

    // create simple high score for single game
    private HighScoreEntity createHighScore(Token token, int score, String time, boolean saveHighScore) {
        HighScoreEntity highScoreEntity = new HighScoreEntity(
            new Date(), token.getUser().getUsername(), score, time, this.getWord(), this.getWrongLettersStr()
        );
        if (saveHighScore) dbh.putHighScoreInDatabase(highScoreEntity);
        return highScoreEntity;
    }

    public void logStatus() {
        System.out.println("----------");
        System.out.println("- word (hidden) = " + word);
        System.out.println("- visibleWord = " + visibleWord);
        System.out.println("- wrongLettersCount = " + wrongLettersCount);
        System.out.println("- usedLetters = " + usedLetters);
        System.out.println("- uniqueLettersOfWord = " + getUniqueLettersOfWord());

        if (isGameOver()) {
            if (gameHasBeenLost) System.out.println("- GAME LOST");
            else if (gameHasBeenWon) System.out.println("- GAME WON");
            System.out.println(" - Score = " + this.currentScore);
            System.out.println(" - Time = " + this.currentSessionTime);
        }
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

    public void getWordsFromWeb(String url, Token token) throws IOException, AuthenticationException {
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

        System.out.println("entities = " + data);
        System.out.println("entities = " + Arrays.asList(data.split("\\s+")));
        possibleWords.clear();
        possibleWords.addAll(new HashSet<String>(Arrays.asList(data.split(" "))));

        System.out.println("possibleWords = " + possibleWords);
        reset(token);
    }

    private int calculateScore(long time) {
        final long timeInMillis = time;
        final int WordLength = this.word.length();
        final int uniqueLetterCount = this.getUniqueLettersOfWord().length();
        final int wrongGuessCount = this.wrongLettersCount;

        // some magic numbers we find appropriate
        int baseLetterScore = 100;
        int baseTimeScore = 100;
        int letterScoreRange = 3333;
        int timeScoreRange = 5000;

        if (gameHasBeenLost) {
            letterScoreRange /= 2;
            timeScoreRange /= 2;
        }

        double bestLetters = uniqueLetterCount; // this should maybe be another metric also taking into account how difficult the word is
        double bestTime = WordLength*1500+500; // should vary with word length. Maybe do something more exciting

        int letterScore = (int) Math.round(baseLetterScore + letterScoreRange * Math.exp(-wrongGuessCount/bestLetters));
        int timeScore = (int) Math.round(baseTimeScore + timeScoreRange * Math.exp(-timeInMillis/bestTime));

        int score = letterScore + timeScore;

        return score;
    }

    public String getUsedLettersStr() {
        StringBuilder sb = new StringBuilder();
        for (String l : this.usedLetters) {
            sb.append(l);
        }
        return sb.toString();
    }

    public String getUniqueLettersOfWord() {
        StringBuilder uniqueLetters = new StringBuilder();

        for (int i=0; i < this.word.length(); i++) {
            char current = this.word.charAt(i);
            if (uniqueLetters.toString().indexOf(current) < 0)
                uniqueLetters.append(current);
        }
        return uniqueLetters.toString();
    }

    public String getWrongLettersStr() {
        StringBuilder sb = new StringBuilder();
        for (String l : this.usedLetters) {
            if (word.contains(l)) continue;
            sb.append(l);
        }
        return sb.toString();
    }

    private static void authenticateUserToken(Token token) throws AuthenticationException {
        if (!TokenHelper.isTokenValid(token))
            throw new AuthenticationException("Failed to authenticate user token.");
    }

    public String getFormattedTime() {
        long time = this.currentSessionTime;
        long minutes = time / TimeUnit.MINUTES.toMillis(1);
        long seconds = time % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);
        long millis = time % TimeUnit.SECONDS.toMillis(1);

        String formatted = String.format("%02d", minutes) +
                String.format(":%02d", seconds) +
                String.format(".%03d", millis);

        return formatted;
    }

}