package me.arthurlins.simbot.core.context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class SessionRotatoryContext implements IContext {

    private String lastAdd = null;

    private LinkedBlockingQueue<String> phrases;

    public SessionRotatoryContext(){
        this(5);
    }

    public SessionRotatoryContext(int contextLength){
        this.phrases = new LinkedBlockingQueue<>(contextLength);
    }

    public void add(String phrase){
        if (this.phrases.remainingCapacity() ==  0){
            this.phrases.remove();
        }
        //final List<String> words = stringToArray(phrase);
        this.phrases.add(phrase);
        this.lastAdd = phrase;
    }

    private List<String> stringToArray(String phrase){
        final String[] words = phrase.split("[ ]");
        final List<String> listWords = Arrays.asList(words);
        listWords.remove(" ");
        return new ArrayList<>(listWords);
    }

    @Override
    public Context getContext() {
        return new Context(new ArrayList<>(phrases));
    }
}
