package dk.localghost.hold17.transport;

import dk.localghost.hold17.dto.HighScore;

import javax.jws.WebService;
import java.util.List;

@WebService
public interface IDatabaseHandler {
    List<HighScore> getHighScoreList();
}