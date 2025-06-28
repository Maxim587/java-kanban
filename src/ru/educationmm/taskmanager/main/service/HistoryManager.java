package ru.educationmm.taskmanager.main.service;

import ru.educationmm.taskmanager.main.model.Task;
import java.util.List;

public interface HistoryManager {
    boolean add(Task task);
    List<Task> getHistory();
}
