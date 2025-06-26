package ru.educationmm.taskmanager.main.service;

import ru.educationmm.taskmanager.main.model.Task;
import java.util.ArrayList;

public interface HistoryManager {

    void add(Task task);
    ArrayList<Task> getHistory();
}
