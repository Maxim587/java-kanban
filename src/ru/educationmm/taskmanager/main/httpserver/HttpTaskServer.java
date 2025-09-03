package ru.educationmm.taskmanager.main.httpserver;

import com.sun.net.httpserver.HttpServer;
import ru.educationmm.taskmanager.main.httpserver.handler.*;
import ru.educationmm.taskmanager.main.service.TaskManager;
import ru.educationmm.taskmanager.main.util.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;


public class HttpTaskServer {

    private static final int PORT = 8080;
    private static final TaskManager taskManager = Managers.getDefault();
    private static HttpServer httpServer;

    public static void main(String[] args) {
        start();
    }

    public static TaskManager getTaskManager() {
        return taskManager;
    }

    public static void start() {
        try {
            BaseHttpHandler baseHttpHandler = new BaseHttpHandler(taskManager);
            httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);

            httpServer.createContext("/tasks", new TaskHandler(baseHttpHandler));
            httpServer.createContext("/epics", new EpicHandler(baseHttpHandler));
            httpServer.createContext("/subtasks", new SubtaskHandler(baseHttpHandler));
            httpServer.createContext("/history", new HistoryHandler(baseHttpHandler));
            httpServer.createContext("/prioritized", new PrioritizedHandler(baseHttpHandler));
            httpServer.start();
        } catch (IOException e) {
            System.out.println("Ошибка запуска сервера");
            e.printStackTrace();
        }
    }

    public static void stop() {
        httpServer.stop(1);
    }
}
