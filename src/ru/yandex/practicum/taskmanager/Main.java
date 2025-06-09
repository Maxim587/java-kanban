package ru.yandex.practicum.taskmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    // TDB возвращать объект Задача при добавлении задачи.
    // Уточнить какие коды в java для выхода

    private static Scanner scanner;
    private static TaskManager taskManager;

    public static void main(String[] args) {
        taskManager = new TaskManager();
        scanner = new Scanner(System.in);

        Task task_ = new Task("task1", "task1 description", taskManager.getTaskId(), TaskStatus.NEW);
        Epic epic_ = new Epic("epic1", "epic1 description", taskManager.getTaskId(), TaskStatus.NEW);
        Subtask subtask_ = new Subtask("subtask1", "subtask1 description", taskManager.getTaskId(), TaskStatus.NEW, 2);

        taskManager.addTask(task_, TaskType.TASK);
        taskManager.addTask(epic_, TaskType.EPIC);
        taskManager.addTask(subtask_, TaskType.SUBTASK);


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
                    System.out.println(getTask());
                    break;
                case "5":
                    updateTask();
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
        String taskType = fillTaskType();
        if (taskType == null) {
            return;
        }

        System.out.print("Введите имя задачи (Enter - для выхода): ");
        String taskName = fillTaskName();
        if (taskName == null) {
            return;
        }

        System.out.print("Введите описание задачи (Enter - для выхода): ");
        String taskDescription = fillTaskDescription();
        if (taskDescription == null) {
            return;
        }

        if (taskType.equals(TaskType.TASK.name())) {
            taskManager.addTask(new Task(taskName, taskDescription, taskManager.getTaskId(),
                    TaskStatus.NEW), TaskType.TASK);
            System.out.println("Задача " + taskName + " добавлена");
        } else if (taskType.equals(TaskType.EPIC.name())) {
            taskManager.addTask(new Epic(taskName, taskDescription, taskManager.getTaskId(),
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
            int subtaskId = taskManager.getTaskId();

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

    static String fillTaskType() {
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

    static String fillTaskName() {
        String taskName = scanner.nextLine();
        if (taskName.isEmpty()) {
            return null;
        }
        return taskName;
    }

    static String fillTaskDescription() {
        String taskDescription = scanner.nextLine();
        if (taskDescription.isEmpty()) {
            return null;
        }
        return taskDescription;
    }

    static TaskStatus fillTaskStatus() {
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
        tasks.forEach((taskId, task) -> System.out.println("  " + task.getName() + " (id:" + taskId + ")"));
        System.out.println("Эпики:");
        //вывод эпиков их подзадач с использованием epicSubtaskMapping с маппингом эпиков и подзадач
        epics.forEach((epicId, epic) -> {
            System.out.println("  " + epic.getName() + " (id:" + epicId + ")");
            if (epicSubtaskMapping.containsKey(epicId)) {
                epicSubtaskMapping.get(epicId).forEach(subtaskId -> {
                    System.out.println("    -" + subtasks.get(subtaskId).getName() + " (id:" + subtaskId + ")");
                });
            }
        });
    }

    static Task getTask() {
        String idStr = "";
        while(!(isCorrectNumber(idStr))) {
            printTaskList();
            System.out.print("Введите номер задачи (0 - для выхода): ");
            idStr = scanner.nextLine();
        }
        if(idStr.equals("0")) {
            return null;
        }
        int id = Integer.parseInt(idStr);

        Task task;
        if (taskManager.getTaskById(id) != null) {
            task = taskManager.getTaskById(id);
        } else if (taskManager.getEpicById(id) != null) {
            task = taskManager.getEpicById(id);
        } else if (taskManager.getSubtaskById(id) != null) {
            task = taskManager.getSubtaskById(id);
        } else {
            System.out.println("Задачи с таким номером нет");
            return null;
        }
        return task;
    }

    static void updateTask () {
        Task task = getTask();
        if (task == null) {
            return;
        }

        String taskType = task.getClass().getSimpleName();

        System.out.println("Заполните атрибуты задачи, которые хотите обновить");
        System.out.print("Введите новое имя задачи (Enter - пропустить): ");
        String taskName = fillTaskName();
        if (taskName == null) {
            taskName = task.getName();
        }

        System.out.print("Введите новое описание задачи (Enter - пропустить): ");
        String taskDescription = fillTaskDescription();
        if (taskDescription == null) {
            taskDescription = task.getDescription();
        }

        TaskStatus taskStatus = task.getStatus();
        if (!"Epic".equals(taskType)) {
            System.out.println("Введите новый статус задачи (Enter - пропустить): ");
            TaskStatus newStatus = fillTaskStatus();
            if (newStatus != null) {
                taskStatus = newStatus;
            }
        }

        switch(taskType) {
            case "Task":
                Task updatedTask = new Task(taskName, taskDescription, task.getId(), taskStatus);
                break;
            case "Epic":
                Epic updatedEpic = new Epic(taskName, taskDescription, task.getId(), taskStatus);
                break;
            case "Subtask":
                Subtask updatedSubtask = new Subtask(taskName, taskDescription, task.getId(),
                        taskStatus, ((Subtask)task).getEpicId());
                break;
            default:
                return;
        }


    }
}
