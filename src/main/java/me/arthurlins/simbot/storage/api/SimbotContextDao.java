package me.arthurlins.simbot.storage.api;

import gnu.trove.map.hash.THashMap;
import me.arthurlins.simbot.core.context.SessionRotatoryContext;

public interface SimbotContextDao {

    void addContext(SessionRotatoryContext sessionRotatoryContext);
    SessionRotatoryContext loadContextByKey(int area);
    THashMap<String, SessionRotatoryContext> getAll();

}
