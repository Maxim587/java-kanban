import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import ru.educationmm.taskmanager.main.model.Epic;
import ru.educationmm.taskmanager.main.model.Subtask;
import ru.educationmm.taskmanager.main.model.TaskStatus;


class EpicTest {

    Epic epic1;

    @BeforeEach
    public void prepare() {
        epic1 = new Epic("name1", "description1");
        epic1.setId(1);
    }

    @Test
    public void epicsShouldBeEqualWhenEqualTheirIds() {
        Epic epic2 = new Epic("name2", "description2");
        epic2.setId(1);
        Assertions.assertEquals(epic1, epic2, "Эпики не равны при одинаковых id");
    }

    @Test
    public void setStatus() {
        Assertions.assertSame(TaskStatus.NEW, epic1.getStatus(), "Новый эпик не в статусе NEW");

        Subtask subtask1 = new Subtask("name", "description", 1, TaskStatus.NEW);
        subtask1.setId(2);
        epic1.addSubtaskToEpic(subtask1);
        Assertions.assertSame(TaskStatus.NEW, epic1.getStatus(), "Эпик не в статусе NEW");

        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        epic1.setStatus();
        Assertions.assertSame(TaskStatus.IN_PROGRESS, epic1.getStatus(), "Эпик не в статусе IN_PROGRESS");

        subtask1.setStatus(TaskStatus.DONE);
        epic1.setStatus();
        Assertions.assertSame(TaskStatus.DONE, epic1.getStatus(), "Эпик не в статусе DONE");

        Subtask subtask2 = new Subtask("name", "description", 1, TaskStatus.NEW);
        subtask2.setId(3);
        epic1.addSubtaskToEpic(subtask2);
        Assertions.assertSame(TaskStatus.IN_PROGRESS, epic1.getStatus(), "Эпик не в статусе IN_PROGRESS");

        epic1.deleteSubtaskInEpic(subtask1);
        epic1.deleteSubtaskInEpic(subtask2);
        epic1.setStatus();
        Assertions.assertSame(TaskStatus.NEW, epic1.getStatus(), "Эпик не в статусе NEW");
    }
}