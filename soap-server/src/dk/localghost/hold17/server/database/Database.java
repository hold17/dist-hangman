package dk.localghost.hold17.server.database;

import dk.localghost.hold17.dto.HighScore;
import dk.localghost.hold17.server.database.entities.HighScoreEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.exception.JDBCConnectionException;

import javax.persistence.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Database {
    @PersistenceUnit(
        // TODO: rename to something production ready
        unitName = "testdatabase"
    )
    private EntityManagerFactory entityManagerFactory;

    private static Properties properties;
    private static Connection connection;

    private Database() {
        properties = loadDatabaseSettingsFromFile();
        createEntityManagerFactory(properties);
        ConnectionTestThread connectionTestThread = new ConnectionTestThread();
        new Thread(connectionTestThread).start();
    }

    // Initialization-on-demand holder idiom (look it up on wikipedia)
    private static class LazyHolder {
        static final Database INSTANCE = new Database();
    }

    public static Database getInstance() {
        return LazyHolder.INSTANCE;
    }


//    private Database() {
//        properties = loadDatabaseSettingsFromFile();
//        createEntityManagerFactory(properties);
//        ConnectionTestThread connectionTestThread = new ConnectionTestThread();
//        new Thread(connectionTestThread).start();
//    }
//
//    public static Database getInstance() {
//        if(instance == null){
//            synchronized (Database.class) {
//                if(instance == null){
//                    instance = new Database();
//                }
//            }
//        }
//        return instance;
//    }

    private Connection createConnectionObject() {
        Connection c = null;
        try {
            // TODO: this operation might be pretty expensive... look into
            c = unWrapSessionFactory().getSessionFactoryOptions().getServiceRegistry().getService(ConnectionProvider.class).getConnection();
        } catch (SQLException e) {
            System.out.println("Something went wrong while getting connection-object");
            e.printStackTrace();
        }
        return c;
    }

    private static Properties loadDatabaseSettingsFromFile() {
        Properties props = new Properties();

        final String currentPath = Paths.get("").toAbsolutePath().toString() + "\\dbsettings.properties";
        final String propertiesPath = currentPath.replace("//", "/");

        try {
            FileInputStream fs = new FileInputStream(propertiesPath);
            props.load(fs);
            fs.close();
        } catch (FileNotFoundException e) {
            System.out.println("dbsettings could not be found on path: " + currentPath + "/");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("File read error");
            e.printStackTrace();
        }

        return props;
    }

    private void createEntityManagerFactory(Properties props) throws ExceptionInInitializerError {
        // TODO: rename to something production ready
        try {
            entityManagerFactory = Persistence.createEntityManagerFactory("testdatabase", props);
        } catch (ExceptionInInitializerError e) {
            System.out.println("Connection to database failed. Check error log!");
            e.printStackTrace();
            throw e;
        }

        connection = createConnectionObject();
    }

    // get Hibernate interface from JPA interface
    private SessionFactory unWrapSessionFactory() {
        return entityManagerFactory.unwrap(SessionFactory.class);
    }

    // get Hibernate interface from JPA interface
    private Session unWrapSession(EntityManager entityManager) {
        return entityManager.unwrap(Session.class);
    }

    public void insertNewHighScore(HighScoreEntity highScoreEntity) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            entityManager.getTransaction().begin();
            entityManager.persist(highScoreEntity);
            entityManager.getTransaction().commit();
        } catch (JDBCConnectionException e) {
            System.out.println("Database connection error. Check Database status.");
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }

    public List<HighScore> getListOfHighScores() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

//        try {
            entityManager.getTransaction().begin();
            List<HighScoreEntity> result = entityManager.createQuery("from HighScoreEntity order by score desc", HighScoreEntity.class).getResultList();
            entityManager.getTransaction().commit();
//        } catch () {

//        } finally {
            entityManager.close();
//        }

        List<HighScore> convertedResult = new ArrayList<>();
        for (HighScoreEntity hs : result) {
            convertedResult.add(new HighScore(hs.getId(), hs.getDate(), hs.getPlayerName(), hs.getScore(), hs.getTime(), hs.getCorrectWord(), hs.getWrongLetters()));
        }
        return convertedResult;
    }



    private class ConnectionTestThread implements Runnable {
        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        public void testConnection() {
            try {
                System.out.println("Checking connection validity");
                if (entityManagerFactory != null) {
                    if (connection.isValid(5)) {
                        System.out.println("Connection still running!");
                        return;
                    } else {
                        System.out.println("Connection broken!");
                        entityManagerFactory.close();
                    }
                }

                try {
                    System.out.println("Attempting service restart");
                    createEntityManagerFactory(properties);
                } catch (ExceptionInInitializerError e) {
                    System.out.println("Failed to reconnect. Trying again in 10 seconds.");
                    e.printStackTrace();
                }

            } catch (SQLException e) {
                System.out.println("testConnection failed");
                e.printStackTrace();
            }
        }

        public void run() {
            System.out.println("ConnectionTestThread Started");
            executor.scheduleAtFixedRate(this::testConnection, 0, 10L, TimeUnit.SECONDS);
        }
    }

}