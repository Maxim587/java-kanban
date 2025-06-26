package ru.educationmm.taskmanager.main;

import ru.educationmm.taskmanager.main.service.TaskManager;
import ru.educationmm.taskmanager.main.util.Managers;


public class Main {

    public static void main(String[] args) {
        TaskManager inMemoryTaskManager = Managers.getDefault();
    }
}