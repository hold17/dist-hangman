package dk.localghost.hold17.dto;

public class WordsApi {

    private String messsage;
    private boolean success;

    public WordsApi() {}

    public String getMesssage() {
        return messsage;
    }

    public void setMesssage(String messsage) {
        this.messsage = messsage;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
