package ru.educationmm.taskmanager.main.service;

import ru.educationmm.taskmanager.main.model.Task;

import java.util.List;

public interface TimePrioritized {

    void addToPrioritizedSet(Task task);

    List<Task> getPrioritizedTasks();

    boolean isIntersectingTasks(Task task1, Task task2);

    boolean checkExistingIntersections(Task task);
}
