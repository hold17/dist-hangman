package dk.localghost.hold17.server.database;

import dk.localghost.hold17.server.database.data.HighScore;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import java.util.Date;
import java.util.List;

public class Database {
    @PersistenceUnit(
            unitName = "testdatabase"
    )
    private EntityManagerFactory entityManagerFactory;
    public Database() {
        createEntityManagerFactory();
    }

    protected void createEntityManagerFactory() {
        entityManagerFactory = Persistence.createEntityManagerFactory( "testdatabase" );
    }

    protected void closeEntityManagerFactory() {
        entityManagerFactory.close();
    }

    public void doStuff() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist( new HighScore( new Date(), "Bob", 123 , 420, "bigword", "qtyshj") );
        entityManager.persist( new HighScore( new Date(), "Robert", 234, 69, "biggerword", "mnzxlk") );
        entityManager.getTransaction().commit();
        entityManager.close();

        entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        List<HighScore> result = entityManager.createQuery("from HighScore", HighScore.class).getResultList();
        for (HighScore highScore : result) {
            System.out.println("HighScore (" + highScore.getDate() + ") : playerName: " + highScore.getPlayerName() + " : score: " + highScore.getScore());
        }
        entityManager.getTransaction().commit();
        entityManager.close();
    }

}
