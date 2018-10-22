package me.arthurlins.simbot.core.context;

public class BestContext {
    private Context context;
    private int score;

    public BestContext(Context context, int score) {
        this.context = context;
        this.score = score;
    }

    public Context getContext() {
        return context;
    }

    public int getScore() {
        return score;
    }
}
