import ru.educationmm.taskmanager.main.service.InMemoryTaskManager;


class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    public void setManager() {
        taskManager = new InMemoryTaskManager();
    }

}