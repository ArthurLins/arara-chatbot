package me.arthurlins.simbot.storage.api;

import gnu.trove.map.hash.THashMap;
import me.arthurlins.simbot.core.answers.QAEntry;

import java.util.List;

public interface SimbotAnswerDao {
    List<QAEntry> getAll();
    void addQAModel(QAEntry qaEntry);
}
