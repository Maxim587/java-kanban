package ru.educationmm.taskmanager.main.model;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, int epicId, TaskStatus status) {
        super(name, description, status);
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
    public String toString() {

        return String.format("%d,%s,%s,%s,%s,%d", super.getId(), TaskType.SUBTASK, super.getName(),
                super.getStatus(), super.getDescription(), epicId);
    }
}
