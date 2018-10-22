package me.arthurlins.simbot.core.context;

import java.util.List;

public class BestContextList {
    private int score;
    private List<String> words;

    public BestContextList(int score, List<String> words){
        this.score = score;
        this.words = words;
    }

    public int getScore() {
        return score;
    }

    public List<String> getWords() {
        return words;
    }

    public boolean exists() {
        return words != null;
    }
}
