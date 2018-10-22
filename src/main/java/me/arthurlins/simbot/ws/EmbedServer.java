package me.arthurlins.simbot.ws;

import com.google.gson.Gson;
import me.arthurlins.simbot.core.Moderator;
import me.arthurlins.simbot.core.answers.AnswersManager;
import me.arthurlins.simbot.core.answers.Response;
import me.arthurlins.simbot.core.answers.Session;
import me.arthurlins.simbot.core.tools.Configuration;

import static spark.Spark.*;

public class EmbedServer {
    private static EmbedServer ourInstance;

    public static EmbedServer getInstance() {
        if (ourInstance == null){
            ourInstance = new EmbedServer();
        }
        return ourInstance;
    }


    public static void initialize(){
        if (ourInstance == null){
            ourInstance = new EmbedServer();
        }
    }

    private EmbedServer() {


        threadPool(Integer.parseInt(Configuration.getInstance().get("webservice-thread-max", "10")),
                Integer.parseInt(Configuration.getInstance().get("webservice-thread-min", "5")),
                Integer.parseInt(Configuration.getInstance().get("webservice-idle-timeout", "3000")));
        if (Boolean.getBoolean(Configuration.getInstance().get("webservice-ssl", "false"))){
            secure(Configuration.getInstance().get("webservice-keystore-file-path"),
                    Configuration.getInstance().get("webservice-keystore-password"),
                    Configuration.getInstance().get("webservice-truststore-file-path"),
                    Configuration.getInstance().get("webservice-truststore-password"));
        }
        port(Integer.parseInt(Configuration.getInstance().get("webservice-port")));

        before((req, res) -> {

            res.header("Access-Control-Allow-Origin", "*");
            get("/responses/total", (request, response) -> AnswersManager.getInstance().total());


            get("/", ((request, response) -> {
                String session = request.queryParams("session");
                final String message = request.queryParams("message");
                if (message == null) {
                    halt(400);
                }
                if (session == null) {
                    session = request.ip();
                }
                if (Moderator.getInstance().isBanned(session)) {
                    halt(403);
                }
                if (Moderator.getInstance().isFlood(session)) {
                    Moderator.getInstance().addFloodCount(session);
                    halt(429);
                }
                return new Gson().toJson(AnswersManager.getInstance().getResponse(message, new Session(session), false), Response.class);
            }));
        });
        init();
    }
}
