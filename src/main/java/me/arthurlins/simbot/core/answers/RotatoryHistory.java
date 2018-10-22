package me.arthurlins.simbot.core.answers;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class RotatoryHistory {

    private LinkedBlockingQueue<String> phrases;

    public RotatoryHistory(){
        this(5);
    }

    public RotatoryHistory(int historySize){
        this.phrases = new LinkedBlockingQueue<>(historySize);
    }

    public void add(String phrase){
        if (this.phrases.remainingCapacity() ==  0){
            this.phrases.remove();
        }
        this.phrases.add(phrase);
    }

    public boolean contains(String phrase){
        return this.phrases.contains(phrase);
    }

}
