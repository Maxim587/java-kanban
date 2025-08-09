import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.educationmm.taskmanager.main.model.Subtask;
import ru.educationmm.taskmanager.main.model.TaskStatus;

import java.time.LocalDateTime;

class SubtaskTest {

    @Test
    public void tasksShouldBeEqualWhenEqualTheirIds() {
        Subtask subtask1 = new Subtask("name1", "description1", 2, TaskStatus.NEW, 15, LocalDateTime.now());
        Subtask subtask2 = new Subtask("name2", "description2", 4, TaskStatus.DONE, 15, LocalDateTime.now());
        subtask1.setId(1);
        subtask2.setId(1);
        Assertions.assertEquals(subtask1, subtask2);
    }
}