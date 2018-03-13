package dk.localghost.hold17.dto;

import java.util.Date;

public class HighScore {
    private Long id;
    private Date date;
    private String playerName;
    private int score;
    private String time;
    private String correctWord;
    private String wrongLetters;

    public HighScore(long id, Date date, String playerName, int score, String time, String correctWord, String wrongLetters) {
        this.id = id;
        this.date = date;
        this.playerName = playerName;
        this.score = score;
        this.time = time;
        this.correctWord = correctWord;
        this.wrongLetters = wrongLetters;
    }

    public Long getId() {
        return id;
    }
    public Date getDate() {
        return date;
    }
    public String getPlayerName() {
        return playerName;
    }
    public int getScore() {
        return score;
    }
    public String getTime() {
        return time;
    }
    public String getCorrectWord() {
        return correctWord;
    }
    public String getWrongLetters() {
        return wrongLetters;
    }
}
