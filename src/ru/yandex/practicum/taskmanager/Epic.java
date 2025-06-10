package ru.yandex.practicum.taskmanager;

public class Epic extends Task {

    public Epic(int id, String name, String description, TaskStatus status, TaskType type) {
        super(id, name, description, status, type);
    }
}
