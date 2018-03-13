package dk.localghost.hold17.server.database;

import dk.localghost.hold17.server.Server;
import dk.localghost.hold17.server.database.data.HighScore;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import java.util.List;

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

    public void insertNewHighScore(HighScore highScore) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(highScore);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public List<HighScore> getListOfHighScores() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        List<HighScore> result = entityManager.createQuery("from HighScore", HighScore.class).getResultList();
        entityManager.getTransaction().commit();
        entityManager.close();
        return result;
    }
}