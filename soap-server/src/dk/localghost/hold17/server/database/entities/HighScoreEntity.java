package dk.localghost.hold17.server.database.entities;

import java.util.Date;
import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table( name = "HIGHSCORES" )
public class HighScoreEntity {
    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "HIGHSCORE_DATE")
    private Date date;

    @Basic
    private String playerName;

    @Basic
    private int score;

    @Basic
    private String time;

    @Basic
    private String correctWord;

    @Basic
    private String wrongLetters;

    public HighScoreEntity() {
        // this form used by Hibernate
    }

    public HighScoreEntity(Date date, String playerName, int score, String time, String correctWord, String wrongLetters) {
        // for application use, to create new events
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

    private void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCorrectWord() {
        return correctWord;
    }

    public void setCorrectWord(String correctWord) {
        this.correctWord = correctWord;
    }

    public String getWrongLetters() {
        return wrongLetters;
    }

    public void setWrongLetters(String wrongLetters) {
        this.wrongLetters = wrongLetters;
    }

}