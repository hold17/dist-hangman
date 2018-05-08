package dk.localghost.hold17.dto;

import java.util.List;

public class Definitions extends WordsApi{
    private String word;
    private List<Definition> definitions;

    public Definitions() {}

    public String getWord() {
        return word;
    }
    public void setWord(String word) {
        this.word = word;
    }
    public List<Definition> getDefinitions() {
        return definitions;
    }
    public void setDefinitions(List<Definition> definitions) {
        this.definitions = definitions;
    }
}
