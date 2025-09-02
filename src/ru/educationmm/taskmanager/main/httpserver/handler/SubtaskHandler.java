package ru.educationmm.taskmanager.main.httpserver.handler;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class SubtaskHandler extends AbstractHandler {

    public SubtaskHandler(BaseHttpHandler baseHttpHandler) {
        super(baseHttpHandler);
    }

    @Override
    protected void handleRequests(HttpExchange exchange) throws IOException {
        baseHttpHandler.handleTaskRequests(exchange);
    }
}
