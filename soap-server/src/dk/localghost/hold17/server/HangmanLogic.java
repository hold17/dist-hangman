package dk.localghost.hold17.server;

import com.sun.xml.internal.ws.util.CompletedFuture;
import dk.localghost.authwrapper.transport.AuthenticationException;
import dk.localghost.hold17.dto.Definitions;
import dk.localghost.hold17.dto.Examples;
import dk.localghost.hold17.dto.Synonyms;
import dk.localghost.hold17.dto.Token;
import dk.localghost.hold17.helpers.FatalServerException;
import dk.localghost.hold17.helpers.InvalidWordException;
import dk.localghost.hold17.helpers.TokenHelper;
import dk.localghost.hold17.server.database.entities.HighScoreEntity;
import dk.localghost.hold17.transport.IHangman;
import dk.localghost.hold17.transport.WordService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.jws.WebService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;

@WebService(endpointInterface = "dk.localghost.hold17.transport.IHangman")
public class HangmanLogic implements IHangman {
    private static DatabaseHandler dbh = new DatabaseHandler();
    private List<String> possibleWords = new ArrayList<>();
    private List<String> wordSynonyms = new ArrayList<>();
    private List<String> usedLetters = new ArrayList<>();
    private String word;
    private String wordDefinition;
    private String wordExampleBefore;
    private String wordExampleAfter;
    private String visibleWord;
    private int gameType;
    private int wrongLettersCount;
    private int correctlyGuessedLettersCount;
    private int currentScore;
    private int totalScore;
    private boolean lastGuessedLetterIsCorrect;
    private boolean gameHasBeenWon;
    private boolean gameHasBeenLost;
    private boolean hasGameBegun;
    private long sessionStartTime, sessionStopTime;
    private long currentSessionTime;
    private long totalTime;
    private long instanceLastActiveTime;

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
        this.gameType = hangman.getGameType();
        this.usedLetters = hangman.getUsedLetters();
        this.visibleWord = hangman.getVisibleWord();
        this.wrongLettersCount = hangman.getWrongLettersCount();
        this.gameHasBeenWon = hangman.isGameWon();
        this.gameHasBeenLost = hangman.isGameLost();
    }

    // getters
    public String getWord() {
        return this.word;
    }

    public String getWordDefinition() {
        return this.wordDefinition;
    }

    public String getWordExampleBefore() {
        return this.wordExampleBefore;
    }

    public String getWordExampleAfter() {
        return this.wordExampleAfter;
    }

    public String getVisibleWord() {
        return this.visibleWord;
    }

    public List<String> getWordSynonyms() {
        return this.wordSynonyms;
    }

    public List<String> getPossibleWords() {
        return this.possibleWords;
    }

    public List<String> getUsedLetters() {
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

    public int getGameType() {
        return this.gameType;
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
//        word = possibleWords.get(new Random().nextInt(possibleWords.size()));
//        word = "car";


            try {
                prepareGameType();
            } catch (InvalidWordException e) {
                System.out.println(e.getMessage());
            }

        updateVisibleWord();
        this.hasGameBegun = true;
        System.out.println(token.getUser().getUsername() + " started a new game. The new word is " + this.word);

        System.out.println("Word definition: " + this.wordDefinition);
        System.out.print("Word synonyms: ");
        for (int i = 0; i < this.wordSynonyms.size(); i++)
            System.out.print(this.wordSynonyms.get(i) + ", ");
        System.out.println();
        System.out.println("Word example: " + this.wordExampleBefore + " " + this.visibleWord + " " + this.wordExampleAfter);
        sessionStartTime = System.currentTimeMillis();
    }

    public void reset(Token token) throws AuthenticationException {
        authenticateUserToken(token);
        reset();
    }

    private void reset() {
        usedLetters.clear();
        wordSynonyms.clear();
        wordDefinition = null;
        wordExampleBefore = null;
        wordExampleAfter = null;
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

    private void prepareGameType() throws InvalidWordException {
//        word = possibleWords.get(new Random().nextInt(possibleWords.size()));
        word = "dhfsh";
        fetchWordDefinition();
        Future<List<String>> completeableSynonyms = fetchWordSynonyms(word);
        Future<List<String>> completableExamples = fetchWordExample(word);
        try {
            wordSynonyms = completeableSynonyms.get();
            List<String> examples = completableExamples.get();
            wordExampleBefore = examples.get(0);
            wordExampleAfter = examples.get(1);
            if (wordExampleBefore == null || wordExampleAfter == null) {
                throw new InvalidWordException("Missing example sentence for word " + word);
            }

        } catch(ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
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

    private void fetchWordDefinition() {
//        CompletableFuture<Void> completableFuture = new CompletableFuture<>();
        WordService.getDefinitionAsync(word).enqueue(new Callback<Definitions>() {
            @Override
            public void onResponse(Call<Definitions> call, Response<Definitions> response) {
                try {
                    wordDefinition = response.body().getDefinitions().get(0).getDefinition();
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Couldn't find definitions for word " + word + ". Trying again...");
                }
            }

            @Override
            public void onFailure(Call<Definitions> call, Throwable t) {
                System.err.println("Couldn't get definitions for " + word);
            }
        });
    }

    private static Future<List<String>> fetchWordSynonyms(final String word) {
        final CompletableFuture<List<String>> completableFuture = new CompletableFuture<>();
        List<String> wordSynonyms = new ArrayList<>();

        Executors.newCachedThreadPool().submit(() -> {
            Synonyms synonyms = WordService.getSynonymsAsync(word).execute().body();
            for (int i = 0; i < synonyms.getSynonyms().size() && i < 6; i++) {
                wordSynonyms.add(synonyms.getSynonyms().get(i));
            }

            return completableFuture.complete(wordSynonyms);
        });

        return completableFuture;
    }



//        WordService.getSynonymsAsync(word).execute(new Callback<Synonyms>() {
//            @Override
//            public void onResponse(Call<Synonyms> call, Response<Synonyms> response) {
//                try {
//                    for (int i = 0; i < response.body().getSynonyms().size() && i < 6; i++) {
//                        wordSynonyms.add(response.body().getSynonyms().get(i));
//                    }
//                } catch(IndexOutOfBoundsException e) {
//                    System.out.println("Couldn't find synonyms for word " + word + ". Trying again...");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Synonyms> call, Throwable t) {
//                System.err.println("Couldn't get synonyms for " + word);
//            }
//        });
//    }

    private static Future<List<String>> fetchWordExample(final String word) throws InvalidWordException{
        final CompletableFuture<List<String>> completableFuture = new CompletableFuture<>();
        List<String> splitExamples = new ArrayList<>();
        Executors.newCachedThreadPool().submit(() -> {

            final Examples examples = WordService.getExampleAsync(word).execute().body();
            final String wordExample = examples.getExamples().get(0);
            final String wordExampleBefore = wordExample.split(word)[0];
            final String wordExampleAfter = wordExample.split(word)[1];
            splitExamples.add(wordExampleBefore);
            splitExamples.add(wordExampleAfter);
//            if (splitExamples.get(0) == null || splitExamples.get(1) == null) {
//                throw new InvalidWordException("Missing example sentence for word " + word);
//            }

            return completableFuture.complete(splitExamples);

        });

        return completableFuture;
//            WordService.getExampleAsync(word).enqueue(new Callback<Examples>() {
//                @Override
//                public void onResponse(Call<Examples> call, Response<Examples> response) {
//                       try {
//                           final String wordExample = response.body().getExamples().get(0);
//                           wordExampleBefore = wordExample.split(word)[0];
//                           wordExampleAfter = wordExample.split(word)[1];
//                       } catch (IndexOutOfBoundsException e) {
//                           System.err.println("No example to fetch.");
//                }
//                }
//
//                @Override
//                public void onFailure(Call<Examples> call, Throwable t) {
//                    System.err.println("Couldn't get example sentence for " + word);
//                }
//            });
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

//        float A = 0.9f; // higher number means bigger penalty for bad performance
//        float B = 0.9f; // ----||----

        // These formulas are hard and I gave up getting them to work :(
        // Fucking plot this shit to figure out some numbers that work
        // Find a way around actual time being higher than best time messing shit up
//        int letterScore = (int) Math.round(baseLetterScore * Math.pow(A, (wrongGuessCount - bestLetters)));
//        int timeScore = (int) Math.round(baseTimeScore * Math.pow(B, (timeInMillis - bestTime)));

        int letterScore = (int) Math.round(baseLetterScore + letterScoreRange * Math.exp(-wrongGuessCount/bestLetters));
        int timeScore = (int) Math.round(baseTimeScore + timeScoreRange * Math.exp(-timeInMillis/bestTime));

        // throw a this into a Math.sqrt() to really fuck over bad players
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