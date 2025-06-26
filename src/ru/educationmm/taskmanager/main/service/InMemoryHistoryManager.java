package ru.educationmm.taskmanager.main.service;

import ru.educationmm.taskmanager.main.model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private final ArrayList<Task> taskHistory = new ArrayList<>(10);

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(taskHistory);
    }

    @Override
    public void add(Task task) {
        if (taskHistory.size() == 10) {
            taskHistory.removeFirst();
        }
        taskHistory.add(task);
    }
}
