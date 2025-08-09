package ru.educationmm.taskmanager.main.model;

import java.time.LocalDateTime;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, int epicId, TaskStatus status, long durationInMinutes, LocalDateTime startTime) {
        super(name, description, status, durationInMinutes, startTime);
        this.epicId = epicId;
    }

    public Subtask(Subtask subtask) {
        super(subtask);
        this.epicId = subtask.getEpicId();
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        String startTime = getStartTime() == null ? "" : getStartTime().format(DATE_TIME_FORMATTER);
        return String.format("%d,%s,%s,%s,%s,%d,%d,%s", getId(), getType(), getName(),
                getStatus(), getDescription(), epicId, getDuration().toMinutes(), startTime);
    }
}
