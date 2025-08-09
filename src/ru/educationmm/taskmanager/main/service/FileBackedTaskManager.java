package ru.educationmm.taskmanager.main.service;

import ru.educationmm.taskmanager.main.exception.ManagerSaveException;
import ru.educationmm.taskmanager.main.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static final String HEADER = "id,type,name,status,description,epic,duration,startTime";
    private static final String NEW_LINE = System.lineSeparator();
    private final File file;

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        int latestTaskId = 0;
        String taskEntry = "";

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            br.readLine();

            while (br.ready()) {
                taskEntry = br.readLine();
                Task task = fromString(taskEntry);

                switch (task.getType()) {
                    case TASK -> fileBackedTaskManager.addTaskToMemory(task);
                    case EPIC -> fileBackedTaskManager.addEpicToMemory((Epic) task);
                    case SUBTASK -> fileBackedTaskManager.addSubtaskToMemory((Subtask) task);
                }

                if (task.getId() > latestTaskId) {
                    latestTaskId = task.getId();
                }
            }
            fileBackedTaskManager.id = latestTaskId;
        } catch (FileNotFoundException e) {
            System.out.println("Ошибка чтения файла. Файл не найден");
            e.printStackTrace();
            return null;
        } catch (IllegalArgumentException | IOException e) {
            System.out.println("Некорректные значения полей в строке: " + taskEntry);
            e.printStackTrace();
            return null;
        }

        return fileBackedTaskManager;
    }

    public static Task fromString(String value) throws IllegalArgumentException {
        String[] taskFields = value.split(",", -1);
        int properNumberOfColumnsInFile = 8;

        if (taskFields.length != properNumberOfColumnsInFile) {
            throw new IllegalArgumentException("Количество полей не соответствует формату");
        }

        int id = Integer.parseInt(taskFields[0].trim());
        TaskType taskType = TaskType.valueOf(taskFields[1].trim().toUpperCase());
        String name = taskFields[2];
        TaskStatus status = TaskStatus.valueOf(taskFields[3].trim().toUpperCase());
        String description = taskFields[4];

        int subtaskParentEpicId = 0;
        if (!taskFields[5].isBlank()) {
            subtaskParentEpicId = Integer.parseInt(taskFields[5].trim());
        }

        long duration = Long.parseLong(taskFields[6].trim());

        LocalDateTime startTime;
        if (taskFields[7].isBlank()) {
            startTime = null;
        } else {
            try {
                startTime = LocalDateTime.parse(taskFields[7], Task.DATE_TIME_FORMATTER);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Некорректный формат startTime в строке: " + value, e);
            }
        }

        switch (taskType) {
            case TASK:
                Task task = new Task(name, description, status, duration, startTime);
                task.setId(id);
                return task;
            case EPIC:
                Epic epic = new Epic(name, description);
                epic.setId(id);
                return epic;
            case SUBTASK:
                Subtask subtask = new Subtask(name, description, subtaskParentEpicId, status, duration, startTime);
                subtask.setId(id);
                return subtask;
        }
        return null;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write(HEADER + NEW_LINE);

            for (Task task : tasks.values()) {
                writer.write(task.toString() + NEW_LINE);
            }

            for (Epic epic : epics.values()) {
                writer.write(epic.toString() + NEW_LINE);
            }

            for (Subtask subtask : subtasks.values()) {
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

    private void addTaskToMemory(Task task) {
        super.addTask(task);
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    private void addEpicToMemory(Epic epic) {
        super.addEpic(epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    private void addSubtaskToMemory(Subtask subtask) {
        super.addSubtask(subtask);
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
