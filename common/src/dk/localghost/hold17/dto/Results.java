package dk.localghost.hold17.dto;

import java.util.List;

public class Results extends WordsApi {

    private String word;
    private List<Result> results;

    public Results() {}

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }
}
