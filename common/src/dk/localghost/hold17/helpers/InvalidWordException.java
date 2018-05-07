package dk.localghost.hold17.helpers;

public class InvalidWordException extends Exception {
    private static String standardMessage = "The word does not meet the criteria. " + " ";
    public InvalidWordException() {
        super();
    }

    public InvalidWordException(String message) {
        super(standardMessage + message);
    }

    public InvalidWordException(Throwable cause) {
        super(cause);
    }
}
