import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.educationmm.taskmanager.main.exception.ManagerSaveException;
import ru.educationmm.taskmanager.main.model.*;
import ru.educationmm.taskmanager.main.service.FileBackedTaskManager;
import ru.educationmm.taskmanager.main.service.TaskManager;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private File file;

    @Override
    public void setManager() {
        taskManager = new FileBackedTaskManager(createTempFile());
    }

    public File createTempFile() {
        try {
            file = File.createTempFile("taskman", ".csv");
            file.deleteOnExit();
            return file;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании временного файла", e);
        }
    }

    @Test
    public void managerShouldLoadTasksFromFile() {
        Task task2 = new Task("test task2", "test description2", TaskStatus.IN_PROGRESS, 15, startTime.plusHours(1));
        taskManager.addTask(task2);
        Task task3 = new Task("test task3", "test description3", TaskStatus.DONE, 15, startTime.plusHours(2));
        taskManager.addTask(task3);
        Epic epic2 = new Epic("test epic2", "test description2");
        taskManager.addEpic(epic2);
        Subtask subtask2 = new Subtask("test subtask2", "test description2", epic2.getId(), TaskStatus.IN_PROGRESS, 15, startTime.plusHours(3));
        taskManager.addSubtask(subtask2);
        Subtask subtask3 = new Subtask("test subtask3", "test description3", epic2.getId(), TaskStatus.DONE, 15, startTime.plusHours(4));
        taskManager.addSubtask(subtask3);

        Assertions.assertDoesNotThrow(() -> FileBackedTaskManager.loadFromFile(file), "Загрузка задач из файла не должна приводить к исключениям");
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
                    () -> Assertions.assertEquals(taskFromFile.getStatus(), taskFromMemory.getStatus(), "Статусы задач не совпадают"),
                    () -> Assertions.assertEquals(taskFromFile.getDuration(), taskFromMemory.getDuration(), "Продолжительность задач не совпадает"),
                    () -> Assertions.assertEquals(taskFromFile.getStartTime(), taskFromMemory.getStartTime(), "Время начала задач не совпадает")
            );
            if ("Subtask".equals(taskFromFile.getClass().getSimpleName())) {
                Assertions.assertEquals(((Subtask) taskFromFile).getEpicId(), ((Subtask) taskFromMemory).getEpicId(), "Эпики подзадач не совпадают");
            }
        }
    }

    @Test
    public void testExceptionsDuringFileParsing() {
        String wrongFormatEntry = "1,TASK,test,NEW,test,,5,03.06.2025 22:22:00,(excessive field)";
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                FileBackedTaskManager.fromString(wrongFormatEntry), "Неправильное количество " +
                "полей в задаче из файла должно приводить к исключению");

        String wrongDateEntry = "1,TASK,test,NEW,test,,5,03.06.2025 22:22:00(wrong date)";
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                FileBackedTaskManager.fromString(wrongDateEntry), "Если дата задачи имеет некорректный формат, " +
                "должно быть выброшено исключение");
    }

    @Test
    public void testExceptionsDuringFileSaving() {
        Assertions.assertDoesNotThrow(taskManager::save, "Сохранение задач в файл не должно приводить к исключениям");

        String illegalFileName = "*:;?><";
        FileBackedTaskManager taskManager1 = new FileBackedTaskManager(new File(illegalFileName));
        Assertions.assertThrows(ManagerSaveException.class, taskManager1::save, "Если файл не может быть сохранен, " +
                "должно быть выброшено исключение");
    }
}
