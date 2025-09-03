package ru.educationmm.taskmanager.main.httpserver.handler;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class PrioritizedHandler extends AbstractHandler {

    public PrioritizedHandler(BaseHttpHandler baseHttpHandler) {
        super(baseHttpHandler);
    }

    @Override
    protected void handleRequests(HttpExchange exchange) throws IOException {
        baseHttpHandler.handlePrioritizedRequest(exchange);
    }
}
