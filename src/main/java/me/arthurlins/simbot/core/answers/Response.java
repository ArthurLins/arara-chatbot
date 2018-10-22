package me.arthurlins.simbot.core.answers;

import java.util.UUID;

public class Response {

    private int qaId;
    private UUID uuid;
    private String question;
    private String text;
    private Session session;

    public Response(UUID uuid, Session session, String question, String text, int qaId) {
        this.uuid = uuid;
        this.text = text;
        this.qaId = qaId;
        this.question = question;
        this.session = session;
    }

    public int getQaId() {
        return qaId;
    }


    public UUID getUuid() {
        return uuid;
    }

    public String getText() {
        return text;
    }

}
