package dk.localghost.hold17.helpers;

public class FatalServerException extends Exception {
    private static String standardMessage = "An internal server error has occurred, please contact a maintainer." + " ";
    public FatalServerException() {
        super();
    }

    public FatalServerException(String message) {
        super(standardMessage + message);
    }

    public FatalServerException(String message, Throwable cause) {
        super(standardMessage + message, cause);
    }

    public FatalServerException(Throwable cause) {
        super(cause);
    }

    public FatalServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(standardMessage + message, cause, enableSuppression, writableStackTrace);
    }

}