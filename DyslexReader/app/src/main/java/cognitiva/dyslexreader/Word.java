package cognitiva.dyslexreader;

import android.util.Pair;

import java.util.ArrayList;

public class Word {

    private String word;
    private Pair<String[], Integer> hyphenation;
    private ArrayList<Pair<String, String>> definitions;

    public Word(String word, Pair<String[], Integer> hyphenation, ArrayList<Pair <String, String>> definitions) {
        this.word = word;
        this.hyphenation = hyphenation;
        this.definitions = definitions;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Pair<String[], Integer> getHyphenation() {
        return hyphenation;
    }

    public void setHyphenation(Pair<String[], Integer> hyphenation) {
        this.hyphenation = hyphenation;
    }

    public ArrayList<Pair<String, String>> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(ArrayList<Pair<String, String>> definitions) {
        this.definitions = definitions;
    }
}
