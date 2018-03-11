package dk.localghost.hold17.server.database;

import dk.localghost.hold17.server.database.data.Highscore;

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
        entityManager.persist( new Highscore( "Bob", new Date() , 123) );
        entityManager.persist( new Highscore( "Robert", new Date(), 234) );
        entityManager.getTransaction().commit();
        entityManager.close();

        entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        List<Highscore> result = entityManager.createQuery("from Highscore", Highscore.class).getResultList();
        for (Highscore highscore : result) {
            System.out.println("Highscore (" + highscore.getDate() + ") : playerName: " + highscore.getPlayerName() + " : score: " + highscore.getScore());
        }
        entityManager.getTransaction().commit();
        entityManager.close();
    }

}
