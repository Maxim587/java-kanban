package ru.educationmm.taskmanager.main.httpserver.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class PrioritizedHandler implements HttpHandler {

    private final BaseHttpHandler baseHttpHandler;

    public PrioritizedHandler(BaseHttpHandler baseHttpHandler) {
        this.baseHttpHandler = baseHttpHandler;
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            baseHttpHandler.handlePrioritizedRequest(exchange);
        } catch (IOException e) {
            System.out.println("Ошибка обработки запроса " + exchange.getRequestURI().getPath());
            e.printStackTrace();
        }
    }
}
