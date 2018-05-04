package dk.localghost.hold17.dto;

import dk.localghost.hold17.transport.IHangman;

import java.util.ArrayList;

public class HangmanGame {
    private ArrayList<String> usedLetters = new ArrayList<>();
    private String visibleWord;
    private String wordDefinition;
    private String wordExampleBefore;
    private String wordExampleAfter;
    private String[] wordSynonyms;
    private int wrongLettersCount;
    private boolean lastGuessedLetterIsCorrect;
    private boolean gameHasBeenWon;
    private boolean gameHasBeenLost;
    private boolean isGameOver;
    private boolean hasGameBegun;
    private String time;
    private int score;

    private String finalGuessWord;

    public HangmanGame() { }

    public HangmanGame(IHangman game) { this.setGame(game); }

    public ArrayList<String> getUsedLetters() {
        return usedLetters;
    }
    public void setUsedLetters(ArrayList<String> usedLetters) {
        this.usedLetters = usedLetters;
    }

    public String getVisibleWord() {
        return visibleWord;
    }

    public void setVisibleWord(String visibleWord) {
        this.visibleWord = visibleWord;
    }

    public int getWrongLettersCount() {
        return wrongLettersCount;
    }

    public void setWrongLettersCount(int wrongLettersCount) {
        this.wrongLettersCount = wrongLettersCount;
    }

    public boolean isLastGuessedLetterIsCorrect() {
        return lastGuessedLetterIsCorrect;
    }

    public void setLastGuessedLetterIsCorrect(boolean lastGuessedLetterIsCorrect) {
        this.lastGuessedLetterIsCorrect = lastGuessedLetterIsCorrect;
    }

    public boolean isGameHasBeenWon() {
        return gameHasBeenWon;
    }

    public void setGameHasBeenWon(boolean gameHasBeenWon) {
        this.gameHasBeenWon = gameHasBeenWon;
    }

    public boolean isGameHasBeenLost() {
        return gameHasBeenLost;
    }

    public void setGameHasBeenLost(boolean gameHasBeenLost) {
        this.gameHasBeenLost = gameHasBeenLost;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
    }

    public boolean isHasGameBegun() {
        return hasGameBegun;
    }

    public void setHasGameBegun(boolean hasGameBegun) {
        this.hasGameBegun = hasGameBegun;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getFinalGuessWord() {
        if (this.isGameOver)
            return finalGuessWord;
        else return "NO CHEATING!!";
    }

    private void setFinalGuessWord(String finalGuessWord) {
        this.finalGuessWord = finalGuessWord;
    }

    public void setGame(IHangman hangman) {
        this.setWrongLettersCount(hangman.getWrongLettersCount());
        this.setLastGuessedLetterIsCorrect(hangman.isLastLetterCorrect());
        this.setUsedLetters(hangman.getUsedLetters());
        this.setVisibleWord(hangman.getVisibleWord());
        this.setGameHasBeenLost(hangman.isGameLost());
        this.setGameHasBeenWon(hangman.isGameWon());
        this.setGameOver(hangman.isGameOver());
        this.setHasGameBegun(hangman.hasGameBegun());
        this.setScore(hangman.getCurrentScore());
        this.setTime(hangman.getFormattedTime());
        this.setFinalGuessWord(hangman.getWord());
    }
}
