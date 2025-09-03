package ru.educationmm.taskmanager.main.httpserver.handler;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class HistoryHandler extends AbstractHandler {

    public HistoryHandler(BaseHttpHandler baseHttpHandler) {
        super(baseHttpHandler);
    }

    @Override
    protected void handleRequests(HttpExchange exchange) throws IOException {
        baseHttpHandler.handleHistoryRequest(exchange);
    }
}
