package me.arthurlins.simbot.core.context;

import com.google.gson.Gson;
import me.arthurlins.simbot.core.tools.TextProcessor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity()
@Table(name="contexts")
public class Context {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "context_sequence")
    private long id;


    @ElementCollection
    @CollectionTable(name = "context_phrases")
    private List<String> phrases;

    public Context(){
        //phrases = new ArrayList<>();
    }

    public Context(List<String> phrases){
        this.phrases = phrases;
    }


    private BestContextList bestContext(String phrase){
        List<String> words = stringToArray(phrase);
        return listBestInContext(words);
    }

    private BestContextList listBestInContext(List<String> list){
        int prev = 0;
        int cont;
        int score = 0;
        List<String> context = null;
        for (String phrase : phrases){
            final List<String> arr = stringToArray(phrase);
            cont = 0;
            for (String word: list){
                if (arr.contains(word)) {
                    cont++;
                    score++;
                }
            }
            if (cont > prev){
                context = arr;
                prev = cont;
            }
        }
        return new BestContextList(score, context);
    }

    public int compare(Context context){
        int totalScore = 0;
        for (String phraseRaw : context.phrases) {
            final List<String> phrase = stringToArray(phraseRaw);
            final BestContextList ctx = listBestInContext(phrase);
            totalScore += ctx.getScore();
        }
        final int diff = (Math.abs(qtdPrases() - context.qtdWords())) - (Math.abs(qtdWords() - context.qtdWords()));
        final int max = Math.max(diff, totalScore);
        final int min = Math.min(diff, totalScore);
        return (max - min);
    }

    public int qtdPrases(){
        return phrases.size();
    }

    public int qtdWords(){
        int soum = 0;
        for (String phrase : phrases){
            final List<String> words = stringToArray(phrase);
            soum += words.size();
        }
        return soum;
    }

    public int size(){
        return qtdWords()+qtdPrases();
    }

    public boolean inContext(String phrase){
        return bestContext(phrase).exists();
    }

    public boolean inContext(String phrase, int tolerance){
        return bestContext(phrase).getScore() >= tolerance;
    }

    private List<String> stringToArray(String phrase){
        String[] words = phrase.split("[ ]");
        for (int i = 0; i < words.length; i++){
            words[i] = TextProcessor.processString(words[i]);
        }
        return new ArrayList<>(Arrays.asList(words));
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
