package ru.educationmm.taskmanager.main.model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Subtask> epicSubtasks;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        epicSubtasks = new ArrayList<>();
    }

    public Epic(Epic epic) {
        super(epic);
        this.epicSubtasks = epic.getEpicSubtasks();
    }

    public List<Subtask> getEpicSubtasks() {
        return new ArrayList<>(epicSubtasks);
    }

    public void setStatus() {
        TaskStatus newStatus;

        if (epicSubtasks.isEmpty()) {
            newStatus = TaskStatus.NEW;
        } else {
            boolean isNew = true;
            boolean isDone = true;

            for (Subtask subtask : epicSubtasks) {
                isNew = isNew && (subtask.getStatus() == TaskStatus.NEW);
                isDone = isDone && (subtask.getStatus() == TaskStatus.DONE);
            }
            if (isNew) {
                newStatus = TaskStatus.NEW;
            } else if (isDone) {
                newStatus = TaskStatus.DONE;
            } else {
                newStatus = TaskStatus.IN_PROGRESS;
            }
        }

        if (this.getStatus() != newStatus) {
            this.setStatus(newStatus);
        }
    }

    public void addSubtaskToEpic(Subtask subtask) {
        epicSubtasks.add(subtask);
        setStatus();
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
        setStatus();
    }

    @Override
    public String toString() {

        return "Epic{" +
                "id=" + super.getId() +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() +
                ", epicSubtasks=" + epicSubtasks +
                '}';
    }
}
