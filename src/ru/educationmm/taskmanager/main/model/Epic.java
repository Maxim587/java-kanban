package ru.educationmm.taskmanager.main.model;

import java.time.Duration;
import java.util.*;

public class Epic extends Task {
    private final List<Subtask> epicSubtasks;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW, 0, null);
        epicSubtasks = new ArrayList<>();
    }

    public Epic(Epic epic) {
        super(epic);
        this.epicSubtasks = epic.getEpicSubtasks();
    }

    public List<Subtask> getEpicSubtasks() {
        return new ArrayList<>(epicSubtasks);
    }

    public void setCalculatedFields() {
        Duration duration = Duration.ofMinutes(0);
        Set<TaskStatus> statuses = new HashSet<>();

        epicSubtasks.forEach(subtask -> {
            duration.plus(subtask.getDuration());
            statuses.add(subtask.getStatus());

            if (getStartTime() == null) {
                setStartTime(subtask.getStartTime());
            } else {
                if (getStartTime().isAfter(subtask.getStartTime())) {
                    setStartTime(subtask.getStartTime());
                }
            }
        });
        setDuration(duration);

        TaskStatus status = TaskStatus.NEW;
        if (statuses.contains(TaskStatus.IN_PROGRESS) ||
                (statuses.contains(TaskStatus.NEW) && statuses.contains(TaskStatus.DONE))) {
            status = TaskStatus.IN_PROGRESS;
        } else if (statuses.contains(TaskStatus.DONE)) {
            status = TaskStatus.DONE;
        }
        setStatus(status);
    }

    public void addSubtaskToEpic(Subtask subtask) {
        epicSubtasks.add(subtask);
        setCalculatedFields();
    }

    public List<Integer> getSubtaskIds() {
        List<Integer> subtaskIds = new ArrayList<>();
        epicSubtasks.forEach(subtask -> subtaskIds.add(subtask.getId()));
        return subtaskIds;
    }

    public void clearSubtasks() {
        epicSubtasks.clear();
    }

    public void deleteSubtaskInEpic(Subtask subtask) {
        epicSubtasks.removeIf(subtaskInEpic -> subtaskInEpic.equals(subtask));
    }

    public void updateSubtaskInEpic(Subtask subtask) {
        epicSubtasks.forEach(subtaskInEpic -> {
            if (subtaskInEpic.equals(subtask)) {
                epicSubtasks.set(epicSubtasks.indexOf(subtaskInEpic), subtask);
            }
        });
        setCalculatedFields();
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        String startTime = getStartTime() == null ? "" : getStartTime().format(DATE_TIME_FORMATTER);
        return String.format("%d,%s,%s,%s,%s,,%d,%s", getId(), getType(), getName(),
                getStatus(), getDescription(), getDuration().toMinutes(), startTime);
    }
}
