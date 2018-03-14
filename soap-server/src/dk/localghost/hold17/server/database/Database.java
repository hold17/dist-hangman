package dk.localghost.hold17.server.database;

import dk.localghost.hold17.dto.HighScore;
import dk.localghost.hold17.server.Server;
import dk.localghost.hold17.server.database.entities.HighScoreEntity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import java.util.*;

public class Database {
    @PersistenceUnit(
            // TODO: rename to something production ready
            unitName = "testdatabase"
    )
    private EntityManagerFactory entityManagerFactory;
    public Database() {
        createEntityManagerFactory();
    }

    protected void createEntityManagerFactory() {
        // TODO: rename to something production ready
        entityManagerFactory = Persistence.createEntityManagerFactory("testdatabase", Server.properties);
    }

    protected void closeEntityManagerFactory() {
        entityManagerFactory.close();
    }

    public void insertNewHighScore(HighScoreEntity highScoreEntity) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(highScoreEntity);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public List<HighScore> getListOfHighScores() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        List<HighScoreEntity> result = entityManager.createQuery("from HighScoreEntity order by score", HighScoreEntity.class).getResultList();
        entityManager.getTransaction().commit();
        entityManager.close();

        List<HighScore> convertedResult = new ArrayList<>();
        for (HighScoreEntity hs : result) {
            convertedResult.add(new HighScore(hs.getId(), hs.getDate(), hs.getPlayerName(), hs.getScore(), hs.getTime(), hs.getCorrectWord(), hs.getWrongLetters()));
        }
        return convertedResult;
    }
}