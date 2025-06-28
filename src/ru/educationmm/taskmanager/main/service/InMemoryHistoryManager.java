package ru.educationmm.taskmanager.main.service;

import ru.educationmm.taskmanager.main.model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    // Привет! Спасибо за наводку) Тоже сначала выбирал, читал эту статью https://javarush.com/groups/posts/1938-linkedlist
    // и остановился на arraylist. Кстати, вот эта ссылка не открывается
    // https://www.javatpoint.com/difference-between-arraylist-and-linkedlist

    private final List<Task> taskHistory = new LinkedList<>();

    @Override
    public List<Task> getHistory() {
        return new LinkedList<>(taskHistory);
    }

    @Override
    public boolean add(Task task) {
        if (task == null) {
            return false;
        }
        if (taskHistory.size() == 10) {
            taskHistory.removeFirst();
        }
        taskHistory.add(task);
        return true;
    }
}
