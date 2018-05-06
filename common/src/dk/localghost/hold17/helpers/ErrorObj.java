package dk.localghost.hold17.helpers;

public class ErrorObj {
    private String error_type;
    private String error_message;

    public ErrorObj() { }

    public ErrorObj(String error_message) {
        this.error_message = error_message;
    }

    public ErrorObj(String error_type, String error_message) {
        this.error_type = error_type;
        this.error_message = error_message;
    }

    public String getError_type() {
        return error_type;
    }

    public void setError_type(String error_type) {
        this.error_type = error_type;
    }

    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }
}
