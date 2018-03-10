package dk.localghost.hold17.server;

import java.io.*;

/**
 * Helper class to make it a bit easier to save and load serialized objects
 */
public class Serializing {
    /**
     * Save a serialized object to a a file
     * @param object
     * @param filename
     * @throws IOException
     */
    public static void save(Serializable object, String filename) throws IOException {
        try (final FileOutputStream fileStream = new FileOutputStream(filename)) {
            try (final ObjectOutputStream objectStream = new ObjectOutputStream(fileStream)) {
                objectStream.writeObject(object);
            }
        }
    }

    /**
     * Load a serialized object from a file
     * @param filename
     * @return serialized object
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Serializable load(String filename) throws IOException, ClassNotFoundException {
        try (final FileInputStream fileStream = new FileInputStream(filename)) {
            try (final ObjectInputStream objectStream = new ObjectInputStream(fileStream)) {
                return (Serializable) objectStream.readObject();
            }
        }
    }
}
