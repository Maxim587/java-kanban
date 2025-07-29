import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ru.educationmm.taskmanager.main.service.*;
import ru.educationmm.taskmanager.main.util.Managers;

class ManagersTest {

    TaskManager taskManager;

    @Test
    public void getDefault() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Assertions.assertInstanceOf(InMemoryHistoryManager.class, historyManager, "Менеджер должен вернуть " +
                "объект InMemoryHistoryManager");
    }

    @Test
    public void getDefaultHistory() {
        taskManager = Managers.getDefault();
        Assertions.assertInstanceOf(FileBackedTaskManager.class, taskManager, "Менеджер должен вернуть " +
                "объект FileBackedTaskManager");
    }

}