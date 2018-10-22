package me.arthurlins.simbot.core;

import me.arthurlins.simbot.storage.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Moderator {

    public static final long FLOOD_TIME = 500;
    public static final long CHANCES = 5;


    private static Moderator ourInstance = new Moderator();

    public static Moderator getInstance() {
        return ourInstance;
    }

    private ConcurrentHashMap<String, Long> sessions;

    private ConcurrentHashMap<String, Integer> floodCounts;

    private List<String> sessionsBanned;

    private Logger logger = LoggerFactory.getLogger(Database.class);


    private Moderator() {
        sessions = new ConcurrentHashMap<>();
        floodCounts = new ConcurrentHashMap<>();
        sessionsBanned = Collections.synchronizedList(new ArrayList<>());
    }

    public boolean isFlood(String session){
        if (!sessions.containsKey(session)){
            sessions.put(session, System.currentTimeMillis());
            return false;
        }
        Long old = sessions.get(session);
        sessions.replace(session, old, System.currentTimeMillis());
        return (System.currentTimeMillis() - old) < FLOOD_TIME;
    }

    public boolean isBanned(String session){
        return sessionsBanned.contains(session);
    }

    public void addFloodCount(String session){
        if (!floodCounts.containsKey(session)){
            floodCounts.put(session, 1);
            return;
        }
        final int newValue = (floodCounts.get(session)+1);
        if (newValue >= CHANCES){
            sessionsBanned.add(session);
            logger.info("Banned session: "+ session);
        }
        floodCounts.replace(session, floodCounts.get(session), newValue);
    }
}
