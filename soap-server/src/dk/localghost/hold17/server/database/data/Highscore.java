package dk.localghost.hold17.server.database.data;

import java.util.Date;
import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table( name = "HIGHSCORES" )
public class Highscore {
    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    private Long id;

    @Basic
    private String playerName;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "HIGHSCORE_DATE")
    private Date date;

    @Basic
    private int score;


    public Highscore() {
        // this form used by Hibernate
    }

    public Highscore(String playerName, Date date, int score) {
        // for application use, to create new events
        this.playerName = playerName;
        this.date = date;
        this.score = score;
    }


    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

}
