package dk.localghost.hold17.server;

import dk.localghost.hold17.dto.HighScore;
import dk.localghost.hold17.server.database.Database;
import dk.localghost.hold17.server.database.entities.HighScoreEntity;
import dk.localghost.hold17.transport.IDatabaseHandler;

import javax.jws.WebService;
import java.util.List;

@WebService(endpointInterface = "dk.localghost.hold17.transport.IDatabaseHandler")
public class DatabaseHandler implements IDatabaseHandler {
    private static final Database db = Database.getInstance();

    public List<HighScore> getHighScoreList() {
        return db.getListOfHighScores();
    }

    public void putHighScoreInDatabase(HighScoreEntity hs) {
        db.insertNewHighScore(hs);
    }

}