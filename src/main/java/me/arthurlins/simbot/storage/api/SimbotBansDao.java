package me.arthurlins.simbot.storage.api;

import java.util.List;

public interface SimbotBansDao {
    List<String> getAll();
    void add();
    void remove();
}
