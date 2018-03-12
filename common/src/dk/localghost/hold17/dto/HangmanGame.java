package dk.localghost.hold17.dto;

import dk.localghost.hold17.transport.IHangman;

import java.util.ArrayList;

public class HangmanGame {
    private ArrayList<String> usedLetters = new ArrayList<>();
    private String visibleWord;
    private int wrongLettersCount;
    private boolean lastGuessedLetterIsCorrect;
    private boolean gameHasBeenWon;
    private boolean gameHasBeenLost;
    private boolean isGameOver;

    public HangmanGame() { }

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

    public void setGame(IHangman hangman) {
        this.setLastGuessedLetterIsCorrect(hangman.isLastLetterCorrect());
        this.setUsedLetters(hangman.getUsedLetters());
        this.setVisibleWord(hangman.getVisibleWord());
        this.setGameHasBeenLost(hangman.isGameLost());
        this.setGameHasBeenWon(hangman.isGameWon());
        this.setGameOver(hangman.isGameOver());
    }
}
