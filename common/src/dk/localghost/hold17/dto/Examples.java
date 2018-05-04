package dk.localghost.hold17.dto;

import java.util.List;

public class Examples {
    private String word;
    private List<String> examples;

    public Examples() {}

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public List<String> getExamples() {
        return examples;
    }

    public void setExamples(List<String> examples) {
        this.examples = examples;
    }
}
