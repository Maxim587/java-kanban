package ru.educationmm.taskmanager.main.service;

import ru.educationmm.taskmanager.main.exception.ManagerSaveException;
import ru.educationmm.taskmanager.main.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import static ru.educationmm.taskmanager.main.httpserver.util.LocalDateTimeAdapter.DATE_TIME_FORMATTER;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static final String HEADER = "id,type,name,status,description,epic,duration,startTime";
    private static final String NEW_LINE = System.lineSeparator();
    private final File file;

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) throws IllegalArgumentException {
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
                startTime = LocalDateTime.parse(taskFields[7], DATE_TIME_FORMATTER);
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

    public void save() throws ManagerSaveException {
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
    public Task addTask(Task task) throws ManagerSaveException {
        Task task1 = super.addTask(task);
        save();
        return task1;
    }

    private void addTaskToMemory(Task task) {
        super.addTask(task);
    }

    @Override
    public Epic addTask(Epic epic) throws ManagerSaveException {
        Epic epic1 = super.addTask(epic);
        save();
        return epic1;
    }

    private void addEpicToMemory(Epic epic) {
        super.addTask(epic);
    }

    @Override
    public Subtask addTask(Subtask subtask) throws ManagerSaveException {
        Subtask subtask1 = super.addTask(subtask);
        save();
        return subtask1;
    }

    private void addSubtaskToMemory(Subtask subtask) {
        super.addTask(subtask);
    }

    // DELETE ALL
    @Override
    public void deleteAllTasks() throws ManagerSaveException {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() throws ManagerSaveException {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() throws ManagerSaveException {
        super.deleteAllSubtasks();
        save();
    }

    // UPDATE
    @Override
    public void updateTask(Task task) throws ManagerSaveException {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateTask(Epic epic) throws ManagerSaveException {
        super.updateTask(epic);
        save();
    }

    @Override
    public void updateTask(Subtask subtask) throws ManagerSaveException {
        super.updateTask(subtask);
        save();
    }

    // DELETE BY ID
    @Override
    public void deleteTaskById(int taskId) throws ManagerSaveException {
        super.deleteTaskById(taskId);
        save();
    }

    @Override
    public void deleteEpicById(int epicId) throws ManagerSaveException {
        super.deleteEpicById(epicId);
        save();
    }

    @Override
    public void deleteSubtaskById(int subtaskId) throws ManagerSaveException {
        super.deleteSubtaskById(subtaskId);
        save();
    }
}
