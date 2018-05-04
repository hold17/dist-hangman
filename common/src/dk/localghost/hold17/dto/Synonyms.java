package dk.localghost.hold17.dto;

import java.util.List;

public class Synonyms {
    private String word;
    private List<String> synonyms;

    public Synonyms() {}

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public List<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
    }
}
