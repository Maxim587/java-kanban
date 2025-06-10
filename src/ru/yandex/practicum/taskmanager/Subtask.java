package ru.yandex.practicum.taskmanager;

public class Subtask extends Task {
    private final int epicId;

    Subtask(int id, String name, String description, TaskStatus status, int epicId, TaskType type) {
        super(id, name, description, status, type);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {

        return "Subtask{" +
                "id=" + super.getId() +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() +
                ", epicId=" + epicId +
                ", type=" + super.getTaskType() +
                '}';
    }
}
