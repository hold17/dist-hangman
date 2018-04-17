package dk.localghost.hold17.server;

import dk.localghost.hold17.dto.HighScore;
import dk.localghost.hold17.server.database.Database;
import dk.localghost.hold17.server.database.entities.HighScoreEntity;
import dk.localghost.hold17.transport.IDatabaseHandler;

import javax.jws.WebService;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

@WebService(endpointInterface = "dk.localghost.hold17.transport.IDatabaseHandler")
public class DatabaseHandler implements IDatabaseHandler {
    private static final Database db = new Database(loadDatabaseSettingsFromFile());

    private static Properties loadDatabaseSettingsFromFile() {
        Properties props = new Properties();

        final String currentPath = Paths.get("").toAbsolutePath().toString();
        final String propertiesPath = (currentPath + "/dbsettings.properties").replace("//", "/");

        try {
            FileInputStream fs = new FileInputStream(propertiesPath);
            props.load(fs);
        } catch (FileNotFoundException e) {
            System.err.println("dbsettings could not be found at path: " + currentPath + "/");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("File read error");
            e.printStackTrace();
        }
        return props;
        //properties = props;
    }

    public List<HighScore> getHighScoreList() {
        return db.getListOfHighScores();
    }

    public void putHighScoreInDatabase(HighScoreEntity hs) {
        db.insertNewHighScore(hs);
    }
}
