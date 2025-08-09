package ru.educationmm.taskmanager.main.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private String description;
    private TaskStatus status;
    private Duration duration;
    private LocalDateTime startTime;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public Task(String name, String description, TaskStatus status, long durationInMinutes, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        if (durationInMinutes < 0) {
            throw new IllegalArgumentException(String.format("Некорректное значение durationInMinutes [%d]", durationInMinutes));
        }
        this.duration = Duration.ofMinutes(durationInMinutes);
        this.startTime = startTime;
    }

    public Task(Task task) {
        this.id = task.getId();
        this.name = task.getName();
        this.description = task.getDescription();
        this.status = task.getStatus();
        this.duration = task.getDuration();
        this.startTime = task.getStartTime();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        String startTime = getStartTime() == null ? "" : getStartTime().format(DATE_TIME_FORMATTER);
        return String.format("%d,%s,%s,%s,%s,,%d,%s",
                id, getType(), name, status, description, duration.toMinutes(), startTime);
    }
}
