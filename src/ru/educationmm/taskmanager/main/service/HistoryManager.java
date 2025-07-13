package ru.educationmm.taskmanager.main.service;

import ru.educationmm.taskmanager.main.model.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}
