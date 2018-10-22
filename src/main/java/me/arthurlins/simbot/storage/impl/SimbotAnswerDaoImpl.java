package me.arthurlins.simbot.storage.impl;

import com.google.gson.Gson;
import me.arthurlins.simbot.core.answers.QAEntry;
import me.arthurlins.simbot.core.answers.Session;
import me.arthurlins.simbot.core.context.Context;
import me.arthurlins.simbot.storage.Database;
import me.arthurlins.simbot.storage.api.SimbotAnswerDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class SimbotAnswerDaoImpl implements SimbotAnswerDao {

    private Logger logger = LoggerFactory.getLogger(SimbotAnswerDaoImpl.class);

    private Database database;


    public SimbotAnswerDaoImpl(){
        database = Database.getInstance();
    }

    @Override
    public List<QAEntry> getAll() {
        try {
            PreparedStatement ps = database.prepareStatement("SELECT * FROM answers");
            ResultSet rs = ps.executeQuery();
            List<QAEntry> lst = new ArrayList<>();
            Context context;
            while (rs.next()){
                context = new Gson().fromJson(rs.getString("context"), Context.class);
                lst.add(new QAEntry(rs.getString("question"), rs.getString("response"), new Session(rs.getString("author")), context));
            }
            rs.close();
            ps.getConnection().close();
            ps.close();
            return lst;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error on database",e);

        }
        return null;
    }

    @Override
    public void addQAModel(QAEntry model) {
        try {
            PreparedStatement ps = database.prepareStatement("INSERT INTO `answers` (`id`, `question`, `response`, `context`, `author`, `time`) VALUES (NULL, ?, ?, ?, ?,CURRENT_TIMESTAMP)");
            ps.setString(1, model.getQuestion());
            ps.setString(2, model.getResponse());
            ps.setString(3, new Gson().toJson(model.getContext()));
            ps.setString(4, model.getSession().toString());
            ResultSet rs = ps.executeQuery();
            ps.close();
            ps.getConnection().close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
