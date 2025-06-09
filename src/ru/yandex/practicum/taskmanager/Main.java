package ru.yandex.practicum.taskmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    // Удалить тестовые данные

    private static Scanner scanner;
    private static TaskManager taskManager;

    public static void main(String[] args) {
        taskManager = new TaskManager();
        scanner = new Scanner(System.in);


        int n;
        n = taskManager.generateTaskId();Task task1 = new Task("task"+n, "task1_description", n, TaskStatus.NEW);
        n = taskManager.generateTaskId();Task task2 = new Task("task"+n, "task2_description", n, TaskStatus.NEW);
        n = taskManager.generateTaskId();Task task3 = new Task("task"+n, "task3_description", n, TaskStatus.NEW);
        n = taskManager.generateTaskId();Epic epic1 = new Epic("epic"+n, "epic1 description", n, TaskStatus.NEW);
        n = taskManager.generateTaskId();Epic epic2 = new Epic("epic"+n, "epic2 description", n, TaskStatus.NEW);
        n = taskManager.generateTaskId();Epic epic3 = new Epic("epic"+n, "epic3 description", n, TaskStatus.NEW);
        n = taskManager.generateTaskId();Subtask subtask1 = new Subtask("subtask"+n, "subtask1 description", n, TaskStatus.NEW, 4);
        n = taskManager.generateTaskId();Subtask subtask2 = new Subtask("subtask"+n, "subtask2 description", n, TaskStatus.NEW, 4);
        n = taskManager.generateTaskId();Subtask subtask3 = new Subtask("subtask"+n, "subtask3 description", n, TaskStatus.NEW, 4);
        n = taskManager.generateTaskId();Subtask subtask4 = new Subtask("subtask"+n, "subtask4 description", n, TaskStatus.NEW, 6);
        n = taskManager.generateTaskId();Subtask subtask5 = new Subtask("subtask"+n, "subtask5 description", n, TaskStatus.NEW, 6);
        n = taskManager.generateTaskId();Subtask subtask6 = new Subtask("subtask"+n, "subtask6 description", n, TaskStatus.NEW, 6);
        n = taskManager.generateTaskId();Subtask subtask7 = new Subtask("subtask"+n, "subtask7 description", n, TaskStatus.NEW, 6);

        taskManager.addTask(task1, TaskType.TASK);
        taskManager.addTask(task2, TaskType.TASK);
        taskManager.addTask(task3, TaskType.TASK);
        taskManager.addTask(epic1, TaskType.EPIC);
        taskManager.addTask(epic2, TaskType.EPIC);
        taskManager.addTask(epic3, TaskType.EPIC);
        taskManager.addTask(subtask1, TaskType.SUBTASK);
        taskManager.addTask(subtask2, TaskType.SUBTASK);
        taskManager.addTask(subtask3, TaskType.SUBTASK);
        taskManager.addTask(subtask4, TaskType.SUBTASK);
        taskManager.addTask(subtask5, TaskType.SUBTASK);
        taskManager.addTask(subtask6, TaskType.SUBTASK);
        taskManager.addTask(subtask7, TaskType.SUBTASK);


        while(true) {
            printMenu();
            String command = scanner.nextLine();

            switch (command) {
                case "1":
                    addTaskRoutine();
                    break;
                case "2":
                    printTaskList();
                    break;
                case "3":
                    taskManager.deleteAllTasks();
                    break;
                case "4":
                    System.out.println(getTaskById());
                    break;
                case "5":
                    updateTask();
                    break;
                case "6":
                    deleteTaskById();
                    break;
                case "0":
                    return;
            }
        }
    }

    static void printMenu() {
        System.out.println("Доступные команды приложения:");
        System.out.println("1 - Добавить задачу");
        System.out.println("2 - Получить список всех задач");
        System.out.println("3 - Удалить все задачи");
        System.out.println("4 - Получить задачу по ID");
        System.out.println("5 - Обновить задачу");
        System.out.println("6 - Удалить задачу по ID");
        System.out.println("7 - Получить задачи эпика");
        System.out.println("0 - Выйти");
        System.out.print("Введите одну из команд: ");
    }

    static void addTaskRoutine() {
        System.out.println("Укажите тип задачи. Для возврата в главное меню нажмите Enter");
        String taskType = promptUserForTaskType();
        if (taskType == null) {
            return;
        }

        System.out.print("Введите имя задачи (Enter - для выхода): ");
        String taskName = promptUserForTaskName();
        if (taskName == null) {
            return;
        }

        System.out.print("Введите описание задачи (Enter - для выхода): ");
        String taskDescription = promptUserForTaskDescription();
        if (taskDescription == null) {
            return;
        }

        if (taskType.equals(TaskType.TASK.name())) {
            taskManager.addTask(new Task(taskName, taskDescription, taskManager.generateTaskId(),
                    TaskStatus.NEW), TaskType.TASK);
            System.out.println("Задача " + taskName + " добавлена");
        } else if (taskType.equals(TaskType.EPIC.name())) {
            taskManager.addTask(new Epic(taskName, taskDescription, taskManager.generateTaskId(),
                    TaskStatus.NEW), TaskType.EPIC);
            System.out.println("Эпик " + taskName + " добавлен");
        } else {
            //добавление подзадачи (SUBTASK)
            String epicIdStr = "-1";
            while(!checkEpicIdNumber(epicIdStr)) {
                printEpicsList();
                System.out.print("Введите ID эпика, в который нужно добавить подзадачу (0 - для выхода): ");
                epicIdStr = scanner.nextLine();
            }
            if (epicIdStr.equals("0")) {
                return;
            }
            int epicId = Integer.parseInt(epicIdStr);
            int subtaskId = taskManager.generateTaskId();

            taskManager.addTask(new Subtask(taskName, taskDescription, subtaskId,
                    TaskStatus.NEW, epicId), TaskType.SUBTASK);
            System.out.println("Подзадача " + taskName + " добавлена в эпик " +
                    taskManager.getEpicById(epicId).getName());
        }
    }

    static void printEpicsList() {
        System.out.println("Эпики:");
        for (Epic epic : taskManager.getEpics().values()) {
            System.out.println(epic.getId() + " - " + epic.getName());
        }
    }

    static boolean isCorrectNumber(String number) {
        String positiveNumberRegex = "\\d+";
        int maxNumberOfDigitsInInt = Integer.toString(Integer.MAX_VALUE).length();
        return number.matches(positiveNumberRegex) &&
                !(number.length() > maxNumberOfDigitsInInt) &&
                Long.parseLong(number) <= Integer.MAX_VALUE;
    }

    static boolean checkEpicIdNumber (String epicIdStr) {
        return epicIdStr.equals("0") ||
                (isCorrectNumber(epicIdStr) && taskManager.checkEpicId(Integer.parseInt(epicIdStr)));
    }

    static String promptUserForTaskType() {
        int counter = 1;
        for (TaskType type : TaskType.values()) {
            System.out.println(counter + " - " + type);
            counter++;
        }
        String taskTypeNumber = scanner.nextLine();
        if (taskTypeNumber.isEmpty()) {
            return null;
        }

        String taskType;

        switch (taskTypeNumber) {
            case "1":
                taskType = "TASK";
                break;
            case "2":
                taskType = "EPIC";
                break;
            case "3":
                if (taskManager.epicsIsEmpty()) {
                    System.out.println("Отсутствуют эпики. Нужно создать эпик для добавления подзадачи");
                    return null;
                }
                taskType = "SUBTASK";
                break;
            default:
                System.out.println("Неправильно указан тип задачи");
                return null;
        }
        return taskType;
    }

    static String promptUserForTaskName() {
        String taskName = scanner.nextLine();
        if (taskName.isEmpty()) {
            return null;
        }
        return taskName;
    }

    static String promptUserForTaskDescription() {
        String taskDescription = scanner.nextLine();
        if (taskDescription.isEmpty()) {
            return null;
        }
        return taskDescription;
    }

    static TaskStatus promptUserForTaskStatus() {
        TaskStatus[] taskStatuses = TaskStatus.values();
        String command = "-";
        while (!(command.isEmpty() || isCorrectNumber(command))) {
            for (int i = 0; i < taskStatuses.length; i++) {
                System.out.println((i + 1) + " - " + taskStatuses[i]);
            }
            command = scanner.nextLine();
        }
        if (command.isEmpty()) {
            return null;
        }

        int statusNumber = Integer.parseInt(command);

        for (int i = 0; i < taskStatuses.length; i++) {
            if (i == (statusNumber - 1)) {
                return taskStatuses[i];
            }
        }
        System.out.println("Введено некорректное значение");
        return null;
    }

    static void printTaskList() {
        HashMap<Integer, Task> tasks = taskManager.getSimpleTasks();
        HashMap<Integer, Epic> epics = taskManager.getEpics();
        HashMap<Integer, Subtask> subtasks = taskManager.getSubtasks();
        HashMap<Integer, ArrayList<Integer>> epicSubtaskMapping = taskManager.getEpicSubtaskMapping();

        System.out.println("Задачи:");
        tasks.forEach((taskId, task) ->
                System.out.println("    " + task.getName() + " (id:" + taskId + " " + task.getStatus() + ")"));
        System.out.println("Эпики:");
        //вывод эпиков их подзадач с использованием epicSubtaskMapping для маппинга эпиков и подзадач
        epics.forEach((epicId, epic) -> {
            System.out.println("    " + epic.getName() + " (id:" + epicId + " " + epic.getStatus() + ")");
            if (epicSubtaskMapping.containsKey(epicId)) {
                epicSubtaskMapping.get(epicId).forEach(subtaskId -> {
                    System.out.println("       - " + subtasks.get(subtaskId).getName() +
                            " (id:" + subtaskId + " " + subtasks.get(subtaskId).getStatus() + ")");
                });
            }
        });
    }

    static int promptUserForTaskId() {
        String idStr = "";
        while(!(isCorrectNumber(idStr))) {
            printTaskList();
            System.out.print("Введите номер задачи (0 - для выхода): ");
            idStr = scanner.nextLine();
        }
        return Integer.parseInt(idStr);
    }

    static Task getTaskById() {
        int taskId = promptUserForTaskId();
        if (taskId == 0) {
            return null;
        }
        Task task;
        if (taskManager.getTaskById(taskId) != null) {
            task = taskManager.getTaskById(taskId);
        } else if (taskManager.getEpicById(taskId) != null) {
            task = taskManager.getEpicById(taskId);
        } else if (taskManager.getSubtaskById(taskId) != null) {
            task = taskManager.getSubtaskById(taskId);
        } else {
            System.out.println("Задачи с таким номером нет");
            return null;
        }
        return task;
    }

    static void updateTask() {
        Task task = getTaskById();
        if (task == null) {
            return;
        }

        String taskType = task.getClass().getSimpleName();
        System.out.println("Заполните атрибуты");
        System.out.print("Введите новое имя задачи (Enter - пропустить): ");
        String taskName = promptUserForTaskName();
        if (taskName == null) {
            taskName = task.getName();
        }

        System.out.print("Введите новое описание задачи (Enter - пропустить): ");
        String taskDescription = promptUserForTaskDescription();
        if (taskDescription == null) {
            taskDescription = task.getDescription();
        }

        TaskStatus taskStatus = task.getStatus();
        if (!"Epic".equals(taskType)) {
            System.out.println("Введите новый статус задачи (Enter - пропустить): ");
            TaskStatus newStatus = promptUserForTaskStatus();
            if (newStatus != null) {
                taskStatus = newStatus;
            }
        }

        switch(taskType) {
            case "Task":
                Task updatedTask = new Task(taskName, taskDescription, task.getId(), taskStatus);
                taskManager.updateTask(updatedTask);
                break;
            case "Epic":
                Epic updatedEpic = new Epic(taskName, taskDescription, task.getId(), taskStatus);
                taskManager.updateTask(updatedEpic);
                break;
            case "Subtask":
                Subtask updatedSubtask = new Subtask(taskName, taskDescription, task.getId(),
                        taskStatus, ((Subtask)task).getEpicId());
                taskManager.updateTask(updatedSubtask);
                break;
        }
    }

    static void deleteTaskById() {
        Task task = getTaskById();
    }
}
