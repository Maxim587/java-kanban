package ru.educationmm.taskmanager.main.service;

import ru.educationmm.taskmanager.main.model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_HISTORY_SIZE = 10;
    private final List<Task> taskHistory = new LinkedList<>();

    @Override
    public List<Task> getHistory() {
        return new LinkedList<>(taskHistory);
    }

    //Да, поторопился тут)  Оставил копирование задач при добавлении в историю, не знаю правильно или нет, но показалось, что
    // в ТЗ есть требование про это "убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных."
    // А статьи имбовые кстати, особенно про compute у мапы в прошлом спринте)

    public void add(Task task) {
        if (taskHistory.size() == MAX_HISTORY_SIZE) {
            taskHistory.removeFirst();
        }
        taskHistory.add(task);
    }
}
