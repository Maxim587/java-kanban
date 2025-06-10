package ru.yandex.practicum.taskmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    private static Scanner scanner;
    private static TaskManager taskManager;

    public static void main(String[] args) {
        taskManager = new TaskManager();
        scanner = new Scanner(System.in);

        while (true) {
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
                    System.out.println("Все задачи удалены");
                    break;
                case "4":
                    Task task = getTaskById();
                    if (task != null) {
                        System.out.println(task);
                    }
                    break;
                case "5":
                    updateTask();
                    break;
                case "6":
                    deleteTaskById();
                    break;
                case "7":
                    printEpicSubtasks();
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
        TaskType taskType = promptUserForTaskType();
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

        switch (taskType) {
            case TASK:
                taskManager.addTask(new Task(taskManager.generateTaskId(), taskName, taskDescription,
                        TaskStatus.NEW, TaskType.TASK));
                System.out.println("Задача " + taskName + " добавлена");
                break;
            case EPIC:
                taskManager.addEpic(new Epic(taskManager.generateTaskId(), taskName, taskDescription,
                        TaskStatus.NEW, TaskType.EPIC));
                System.out.println("Эпик " + taskName + " добавлен");
                break;
            case SUBTASK:
                String epicIdStr = "-1";
                while (!checkEpicIdNumber(epicIdStr)) {
                    printEpicsList();
                    System.out.print("Введите ID эпика, в который нужно добавить подзадачу (0 - для выхода): ");
                    epicIdStr = scanner.nextLine();
                }
                if (epicIdStr.equals("0")) {
                    return;
                }
                int epicId = Integer.parseInt(epicIdStr);
                int subtaskId = taskManager.generateTaskId();

                taskManager.addSubtask(new Subtask(subtaskId, taskName, taskDescription,
                        TaskStatus.NEW, epicId, TaskType.SUBTASK));
                System.out.println("Подзадача " + taskName + " добавлена в эпик " +
                        taskManager.getEpicById(epicId).getName());
                break;
        }
    }

    static boolean isCorrectNumber(String number) {
        String positiveNumberRegex = "\\d+";
        int maxNumberOfDigitsInInt = Integer.toString(Integer.MAX_VALUE).length();
        return number.matches(positiveNumberRegex) &&
                !(number.length() > maxNumberOfDigitsInInt) &&
                Long.parseLong(number) <= Integer.MAX_VALUE;
    }

    static boolean checkEpicIdNumber(String epicIdStr) {
        return epicIdStr.equals("0") ||
                (isCorrectNumber(epicIdStr) && taskManager.checkEpicId(Integer.parseInt(epicIdStr)));
    }

    static TaskType promptUserForTaskType() {
        int counter = 1;
        for (TaskType type : TaskType.values()) {
            System.out.println(counter + " - " + type);
            counter++;
        }
        String taskTypeNumber = scanner.nextLine();
        if (taskTypeNumber.isEmpty()) {
            return null;
        }

        TaskType taskType;

        switch (taskTypeNumber) {
            case "1":
                taskType = TaskType.TASK;
                break;
            case "2":
                taskType = TaskType.EPIC;
                break;
            case "3":
                if (taskManager.epicsIsEmpty()) {
                    System.out.println("Отсутствуют эпики. Нужно создать эпик для добавления подзадачи");
                    return null;
                }
                taskType = TaskType.SUBTASK;
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
        if (taskManager.tasksIsEmpty() && taskManager.epicsIsEmpty()) {
            System.out.println("Список задач пуст");
            return;
        }
        HashMap<Integer, Task> tasks = taskManager.getTasks();
        HashMap<Integer, Epic> epics = taskManager.getEpics();
        HashMap<Integer, Subtask> subtasks = taskManager.getSubtasks();
        HashMap<Integer, ArrayList<Integer>> epicSubtaskMapping = taskManager.getEpicSubtaskMapping();

        System.out.println("Задачи:");
        tasks.forEach((taskId, task) ->
                System.out.println("    " + task.getName() + " (id:" + taskId + " " + task.getStatus() + ")"));
        System.out.println("Эпики:");
        //вывод эпиков и подзадач с использованием epicSubtaskMapping для маппинга эпиков и подзадач
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

    static void printEpicsList() {
        System.out.println("Эпики:");
        for (Epic epic : taskManager.getEpics().values()) {
            System.out.println(epic.getId() + " - " + epic.getName());
        }
    }

    static int promptUserForTaskId(boolean onlyEpics) {
        String idStr = "";
        while (!(isCorrectNumber(idStr))) {
            if (onlyEpics) {
                printEpicsList();
            } else {
                printTaskList();
            }
            System.out.print("Введите номер задачи (0 - для выхода): ");
            idStr = scanner.nextLine();
        }
        return Integer.parseInt(idStr);
    }

    static Task getTaskById() {
        if (taskManager.tasksIsEmpty() && taskManager.epicsIsEmpty()) {
            System.out.println("Список задач пуст");
            return null;
        }
        int taskId = promptUserForTaskId(false);
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

        TaskType taskType = task.getTaskType();
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
        if (!(taskType == TaskType.EPIC)) {
            System.out.println("Введите новый статус задачи (Enter - пропустить): ");
            TaskStatus newStatus = promptUserForTaskStatus();
            if (newStatus != null) {
                taskStatus = newStatus;
            }
        }

        switch (taskType) {
            case TASK:
                Task updatedTask = new Task(task.getId(), taskName, taskDescription, taskStatus, TaskType.TASK);
                taskManager.updateTask(updatedTask);
                break;
            case EPIC:
                Epic updatedEpic = new Epic(task.getId(), taskName, taskDescription, taskStatus, TaskType.EPIC);
                taskManager.updateEpic(updatedEpic);
                break;
            case SUBTASK:
                Subtask updatedSubtask = new Subtask(task.getId(), taskName, taskDescription,
                        taskStatus, ((Subtask) task).getEpicId(), TaskType.SUBTASK);
                taskManager.updateSubtask(updatedSubtask);
                break;
        }
    }

    static void deleteTaskById() {
        Task task = getTaskById();
        if (task == null) {
            return;
        }

        TaskType taskType = task.getTaskType();
        int taskId = task.getId();

        switch (taskType) {
            case TASK:
                taskManager.deleteTaskByIdNew(taskId);
                break;
            case EPIC:
                taskManager.deleteEpicById(taskId);
                break;
            case SUBTASK:
                taskManager.deleteSubtaskById(taskId);
                break;
        }
    }

    static void printEpicSubtasks() {
        if (taskManager.epicsIsEmpty()) {
            System.out.println("Список задач пуст");
            return;
        }
        int epicId = promptUserForTaskId(true);
        if (epicId == 0) {
            return;
        } else if (!taskManager.checkEpicId(epicId)) {
            System.out.println("Введен неправильный номер эпика");
            return;
        } else if (!taskManager.epicHasSubtasks(epicId)) {
            System.out.println("Эпик " + taskManager.getEpicById(epicId).getName() + " не содержит подзадач");
            return;
        }

        ArrayList<Subtask> epicSubtasks = taskManager.getEpicSubtasks(epicId);
        System.out.println("Подзадачи эпика " + taskManager.getEpicById(epicId).getName() + ":");
        epicSubtasks.forEach(subtask ->
                System.out.println("   - " + subtask.getName() +
                        " (id:" + subtask.getId() + " " + subtask.getStatus() + ")"));
    }
}
