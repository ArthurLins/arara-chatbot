package me.arthurlins.simbot.core.answers;

import me.arthurlins.simbot.core.context.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

class QA {

    private int id;
    private Double heat;
    private Context context;
    private List<String> responses;
    private List<String> questions;

    QA(int id, String question, String fristResponse, Context context){
        this.id = id;
        this.responses = new ArrayList<>();
        this.questions = new ArrayList<>();
        this.context = context;
        addQuestion(question);
        addResponse(fristResponse);
    }

    void addQuestion(String response){
        if (!questions.contains(response))
            questions.add(response);
    }

    String getRandomQuestion(){
        int rIndex = new Random().nextInt(questions.size());
        return questions.get(rIndex);
    }


    void addResponse(String response){
        if (!responses.contains(response))
            responses.add(response);
    }

    String getRandomResponse(){
        int rIndex = new Random().nextInt(responses.size());
        return responses.get(rIndex);
    }


    //public List<String> getResponses() {
       // return responses;
   // }

    //public List<String> getQuestions() {
        //return questions;
    //}


    public int getId() {
        return id;
    }

    public Context getContext() {
        return context;
    }
}
