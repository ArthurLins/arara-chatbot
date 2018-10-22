package me.arthurlins.simbot.storage.impl;

import me.arthurlins.simbot.core.answers.QAEntry;
import me.arthurlins.simbot.storage.api.SimbotAnswerDao;

import javax.persistence.*;
import java.util.List;


public class SimbotAnswerDaoImp2 implements SimbotAnswerDao {

    private EntityManager entityManager = Persistence.createEntityManagerFactory("Simbot")
            .createEntityManager();

    @Override
    public List<QAEntry> getAll() {

        return entityManager.createQuery("SELECT a FROM QAEntry a ", QAEntry.class).getResultList();
        //System.out.println(e.getContext().qtdPrases());
        //return null;
    }

    @Override
    public void addQAModel(QAEntry qaEntry) {
        System.out.println(qaEntry.getQuestion()+"|"+qaEntry.getResponse());
        entityManager.getTransaction().begin();
        entityManager.persist(qaEntry);
        entityManager.flush();
        entityManager.clear();
        entityManager.getTransaction().commit();


    }
}
