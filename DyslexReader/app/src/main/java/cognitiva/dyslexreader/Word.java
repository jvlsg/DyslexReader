package cognitiva.dyslexreader;

import android.util.Pair;

import java.util.ArrayList;

public class Word {

    private String word;
    private Pair<String[], Integer> hyphenation;
    private ArrayList<Pair<String, String>> definitions;
    private boolean audio;

    public Word(String word, Pair<String[], Integer> hyphenation, ArrayList<Pair <String, String>> definitions, boolean audio) {
        this.word = word;
        this.hyphenation = hyphenation;
        this.definitions = definitions;
        this.audio = audio;
    }

    public static String removePunctuation(String w) {
        StringBuilder builder = new StringBuilder();

        for (char c : w.toLowerCase().toCharArray()) {
            if (Character.isLetter(c))
                builder.append(c);
        }

        return builder.toString();
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

    public boolean hasAudio() {
        return audio;
    }

    public void setAudio(boolean audio) {
        this.audio = audio;
    }
}
