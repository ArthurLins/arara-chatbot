package me.arthurlins.simbot.core.context;


import java.time.Instant;
import java.util.Date;

public class Contexted<T> {
    private Context context;
    private Date data;
    private T t;

    public Contexted(Context context, T t) {
        this.context = context;
        this.t = t;
        this.data = Date.from(Instant.now());
    }

    public Date getData() {
        return data;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }
}
