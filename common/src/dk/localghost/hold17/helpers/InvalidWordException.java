package dk.localghost.hold17.helpers;

public class InvalidWordException extends Exception {
    private static String standardMessage = " does not meet the criteria. Finding another word.";
    public InvalidWordException() {
        super();
    }

    public InvalidWordException(String message) {
        super("'" + message + "'" + standardMessage);
    }

    public InvalidWordException(Throwable cause) {
        super(cause);
    }
}
