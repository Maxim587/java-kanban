package ru.educationmm.taskmanager.main.httpserver.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

abstract class AbstractHandler implements HttpHandler {
    protected final BaseHttpHandler baseHttpHandler;

    public AbstractHandler(BaseHttpHandler baseHttpHandler) {
        this.baseHttpHandler = baseHttpHandler;
    }

    protected abstract void handleRequests(HttpExchange exchange) throws IOException;


    @Override
    public void handle(HttpExchange exchange) {
        try {
            handleRequests(exchange);
        } catch (IOException e) {
            System.out.println("Ошибка обработки запроса " + exchange.getRequestURI().getPath());
            e.printStackTrace();
        }
    }
}
