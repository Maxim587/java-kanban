package ru.educationmm.taskmanager.main.service;

import ru.educationmm.taskmanager.main.model.Task;

import java.util.*;

public class Prioritized {

    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    public void addToPrioritizedSet(Task task) {
        if (task.getStartTime() == null) {
            return;
        }
        prioritizedTasks.add(task);
    }

    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }

    public boolean isIntersectingTasks(Task task1, Task task2) {
        TreeSet<Task> cmp = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        cmp.add(task1);
        cmp.add(task2);

        return !cmp.first().getEndTime().isBefore(cmp.last().getStartTime());
    }

    public Optional<Task> checkExistingIntersections(Task task) {
        return prioritizedTasks.stream()
                .filter(task0 -> task0.getId() != task.getId() && isIntersectingTasks(task0, task))
                .findFirst();
    }

    public void removeTasks(Collection<? extends Task> tasks) {
        tasks.forEach(prioritizedTasks::remove);
    }

    public <T extends Task> void removeSingle(T task) {
        prioritizedTasks.remove(task);
    }
}
