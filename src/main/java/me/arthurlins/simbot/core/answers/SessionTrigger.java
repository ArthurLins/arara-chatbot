package me.arthurlins.simbot.core.answers;

import me.arthurlins.simbot.core.context.Contexted;

public class SessionTrigger extends Session{

    private Contexted<String> trigger;
    //private Session session;

    public SessionTrigger(Contexted<String> trigger, Session session) {
        super(session.toString());
        this.trigger = trigger;
    }

    public Contexted<String> getTrigger() {
        return trigger;
    }

    public Session getSession() {
        return this;
    }
}
