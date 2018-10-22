package me.arthurlins.simbot.storage.impl;

import gnu.trove.map.hash.THashMap;
import me.arthurlins.simbot.core.context.SessionRotatoryContext;
import me.arthurlins.simbot.storage.Database;
import me.arthurlins.simbot.storage.api.SimbotContextDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimbotContextDaoImpl implements SimbotContextDao {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Database database;

    public SimbotContextDaoImpl(){
        database = Database.getInstance();
    }

    @Override
    public void addContext(SessionRotatoryContext sessionRotatoryContext) {

    }

    @Override
    public SessionRotatoryContext loadContextByKey(int area) {
        return null;
    }

    @Override
    public THashMap<String, SessionRotatoryContext> getAll() {
        return null;
    }
}
