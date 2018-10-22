package me.arthurlins.simbot.storage;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.arthurlins.simbot.core.answers.AnswersManager;
import me.arthurlins.simbot.core.tools.Configuration;
import me.arthurlins.simbot.storage.api.SimbotAnswerDao;
import me.arthurlins.simbot.storage.impl.SimbotAnswerDaoImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {

    private Logger logger = LoggerFactory.getLogger(Database.class);

    private static Database instance;
    public static Database getInstance(){
        if (instance == null){
            instance = new Database();
        }
        return instance;
    }

    public static void initialize(){
        if (instance == null){
            instance = new Database();
        }
    }

    private DataSource source;

    private Database(){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(Configuration.getInstance().get("jdbcUrl"));
        config.setUsername(Configuration.getInstance().get("username"));
        config.setPassword(Configuration.getInstance().get("password"));
        config.addDataSourceProperty("characterEncoding","utf8");
        config.addDataSourceProperty("useUnicode","true");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("autoReconnect",true);
        config.addDataSourceProperty("tcpKeepAlive", true);
        config.setMaximumPoolSize(50);
        config.setMinimumIdle(0);
        config.setIdleTimeout(10);
        source = new HikariDataSource(config);
        logger.info("Database started.");
    }

    public DataSource getSource() {
        return source;
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return source.getConnection().prepareStatement(sql);
    }

    public ResultSet query(String query, Object... objs) throws Exception {
        try {
            final Connection connection = source.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);
            int index = 1;
            for (Object obj : objs){
                if (obj instanceof String){
                    ps.setString(index, (String)obj);
                } else if (obj instanceof Integer){
                    ps.setInt(index, (Integer)obj);
                } else if (obj instanceof Double){
                    ps.setDouble(index, (Double)obj);
                } else if (obj instanceof Boolean){
                    ps.setBoolean(index, (Boolean)obj);
                } else {
                    connection.close();
                    ps.close();
                    throw new Exception("Not supported data type");
                }
                index++;
            }
            final ResultSet rs = ps.executeQuery();
            ps.close();
            connection.close();
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
