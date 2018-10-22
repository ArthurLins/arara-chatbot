package me.arthurlins.simbot.core.answers;

import com.google.gson.annotations.JsonAdapter;
import me.arthurlins.simbot.core.context.Context;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.Instant;
import java.util.Date;

@Entity
@Table(name="answers_hbl")
public class QAEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "answers_sequence")
    private long id;

    private String question;
    private String response;
    //@JsonAdapter(Session.class)
    //@Column(name = "author")

    private Session session;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Context context;

    private int heat;
    //@Column(columnDefinition = "datetime default 'pt_br'")
    private String language = "pt_br";

    private Date time = Date.from(Instant.now());


    public QAEntry(String question, String response, Session session, Context context){
        this.question = question;
        this.response = response;
        this.session = session;
        this.context = context;
    }

    public QAEntry() {
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Context getContext() {
        return context;
    }

}
