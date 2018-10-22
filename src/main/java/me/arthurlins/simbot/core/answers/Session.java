package me.arthurlins.simbot.core.answers;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Session {

    //private static final long serialVersionUID = 1L;
    @Column
    private String identifier;

    public Session(String identifier) {
        this.identifier = identifier;
    }

    public Session() {
    }

//    String getIdentifier() {
//        return identifier;
//    }

    @Override
    public String toString() {
        return identifier;
    }
}
