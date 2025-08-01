import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.educationmm.taskmanager.main.model.*;
import ru.educationmm.taskmanager.main.service.FileBackedTaskManager;
import ru.educationmm.taskmanager.main.service.TaskManager;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileBackedTaskManagerTest extends InMemoryTaskManagerTest {

    private File file;

    @Override
    public void setManager() {
        taskManager = new FileBackedTaskManager(createTempFile());
    }

    private File createTempFile() {
        try {
            file = File.createTempFile("taskman", ".csv");
            file.deleteOnExit();
            return file;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании временного файла", e);
        }
    }

    @Test
    public void loadFromFile() {
        Task task2 = new Task("test task2", "test description2", TaskStatus.IN_PROGRESS);
        taskManager.addTask(task2);
        Task task3 = new Task("test task3", "test description3", TaskStatus.DONE);
        taskManager.addTask(task3);
        Epic epic2 = new Epic("test epic2", "test description2");
        taskManager.addEpic(epic2);
        Subtask subtask2 = new Subtask("test subtask2", "test description2", epic2.getId(), TaskStatus.IN_PROGRESS);
        taskManager.addSubtask(subtask2);
        Subtask subtask3 = new Subtask("test subtask3", "test description3", epic2.getId(), TaskStatus.DONE);
        taskManager.addSubtask(subtask3);

        TaskManager taskManagerFromFile = FileBackedTaskManager.loadFromFile(file);
        Assertions.assertNotNull(taskManagerFromFile, "Объект fileBackedTaskManager должен быть создан");

        for (TaskType type : TaskType.values()) {
            switch (type) {
                case TASK -> compareTasks(taskManagerFromFile.getTasks(), taskManager.getTasks());
                case EPIC -> compareTasks(taskManagerFromFile.getEpics(), taskManager.getEpics());
                case SUBTASK -> compareTasks(taskManagerFromFile.getSubtasks(), taskManager.getSubtasks());
            }
        }
    }

    private void compareTasks(List<? extends Task> tasksFromFile, List<? extends Task> tasksFromMemory) {
        int index;

        for (Task taskFromMemory : tasksFromMemory) {
            index = tasksFromFile.indexOf(taskFromMemory);
            Assertions.assertNotEquals(-1, index, "Задача не найдена в файле");
            Task taskFromFile = tasksFromFile.get(index);
            Assertions.assertAll("Задачи не равны",
                    () -> Assertions.assertEquals(taskFromFile.getId(), taskFromMemory.getId(), "Id задач не совпадают"),
                    () -> Assertions.assertEquals(taskFromFile.getName(), taskFromMemory.getName(), "Названия задач не совпадают"),
                    () -> Assertions.assertEquals(taskFromFile.getDescription(), taskFromMemory.getDescription(), "Описания задач не совпадают"),
                    () -> Assertions.assertEquals(taskFromFile.getStatus(), taskFromMemory.getStatus(), "Статусы задач не совпадают")
            );
            if ("Subtask".equals(taskFromFile.getClass().getSimpleName())) {
                Assertions.assertEquals(((Subtask) taskFromFile).getEpicId(), ((Subtask) taskFromMemory).getEpicId(), "Эпики подзадач не совпадают");
            }
        }
    }


}
