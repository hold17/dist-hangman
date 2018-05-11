package dk.localghost.hold17.helpers;

public class ErrorBuilder {
    public static ErrorObj buildServiceUnavailable() {
        return buildServiceUnavailable(null);
    }

    public static ErrorObj buildServiceUnavailable(final String serviceType) {
        final ErrorObj err = new ErrorObj();
        final String service = serviceType == null ? "Service" : serviceType + " service";

        err.setError_type("downtime_error");
        err.setError_message(service + " is temporarily unavailable due to maintenance. Please try again later, or contact us at hold17@protonmail.com.");

        return err;
    }

}