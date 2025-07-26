package ru.educationmm.taskmanager.main.service;

import ru.educationmm.taskmanager.main.exception.ManagerSaveException;
import ru.educationmm.taskmanager.main.model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static final String HEADER = "id,type,name,status,description,epic";
    private static final String NEW_LINE = System.lineSeparator();
    private final File file;

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
    }

    public FileBackedTaskManager(File file,
                                 Map<Integer, Task> tasks,
                                 Map<Integer, Epic> epics,
                                 Map<Integer, Subtask> subtasks,
                                 int id) {
        super.tasks = tasks;
        super.epics = epics;
        super.subtasks = subtasks;
        super.id = id;
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) throws IOException {

        if (!file.isFile()) {
            throw new FileNotFoundException("Указанный файл не существует");
        }

        Map<Integer, Task> tasks = new HashMap<>();
        Map<Integer, Epic> epics = new HashMap<>();
        Map<Integer, Subtask> subtasks = new HashMap<>();
        String taskEntry;
        int latestTaskId = 0;

        List<String> tasksFromFile = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

        for (int i = 1; i < tasksFromFile.size(); i++) {
            taskEntry = tasksFromFile.get(i);
            Task task = fromString(taskEntry);

            if (task == null) {
                return null;
            }

            if (task.getId() > latestTaskId) {
                latestTaskId = task.getId();
            }

            switch (task.getClass().getSimpleName()) {
                case "Epic" -> epics.put(task.getId(), (Epic) task);
                case "Subtask" -> {
                    Epic subtaskEpic = epics.get(((Subtask) task).getEpicId());
                    subtasks.put(task.getId(), (Subtask) task);
                    subtaskEpic.addSubtaskToEpic((Subtask) task);
                }
                case "Task" -> tasks.put(task.getId(), task);
                default -> {
                    return null;
                }
            }
        }
        return new FileBackedTaskManager(file, tasks, epics, subtasks, latestTaskId);
    }

    private static Task fromString(String value) {
        String[] taskFields;
        TaskType taskType;
        int id;
        String name;
        String description;
        TaskStatus status;
        int subtaskParentEpicId = 0;

        taskFields = value.split(",", -1);
        if (taskFields.length != 6) {
            System.out.println("Количество полей не соответствует формату в строке\n" + value);
            return null;
        }

        try {
            id = Integer.parseInt(taskFields[0].trim());
            if (!taskFields[5].isBlank()) {
                subtaskParentEpicId = Integer.parseInt(taskFields[5].trim());
            }
        } catch (NumberFormatException e) {
            System.out.format("Некорректное значение поля в строке %n%s", value);
            e.printStackTrace();
            return null;
        }

        try {
            taskType = TaskType.valueOf(taskFields[1].trim().toUpperCase());
            status = TaskStatus.valueOf(taskFields[3].trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.format("Некорректное значение поля в строке %n%s", value);
            e.printStackTrace();
            return null;
        }

        name = taskFields[2];
        description = taskFields[4];

        switch (taskType) {
            case TASK:
                Task task = new Task(name, description, status);
                task.setId(id);
                return task;
            case EPIC:
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus();
                return epic;
            case SUBTASK:
                Subtask subtask = new Subtask(name, description, subtaskParentEpicId, status);
                subtask.setId(id);
                return subtask;
        }
        return null;
    }

    private void save() {
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write(HEADER + NEW_LINE);

            for (Task task : super.tasks.values()) {
                writer.write(task.toString() + NEW_LINE);
            }

            for (Epic epic : super.epics.values()) {
                writer.write(epic.toString() + NEW_LINE);
            }

            for (Subtask subtask : super.subtasks.values()) {
                writer.write(subtask.toString() + NEW_LINE);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи файла", e);
        }
    }

    // ADD
    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    // DELETE ALL
    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    // UPDATE
    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    // DELETE BY ID
    @Override
    public void deleteTaskById(int taskId) {
        super.deleteTaskById(taskId);
        save();
    }

    @Override
    public void deleteEpicById(int epicId) {
        super.deleteEpicById(epicId);
        save();
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
        super.deleteSubtaskById(subtaskId);
        save();
    }
}
