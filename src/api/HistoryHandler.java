package api;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager manager;
    Gson gson;

    public HistoryHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathSplit = exchange.getRequestURI().getPath().split("/");
        String requestMethod = exchange.getRequestMethod();

        if (requestMethod.equals("GET")) {
            handleGetHistory(exchange, pathSplit);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleGetHistory(HttpExchange exchange, String[] pathSplit) throws IOException {
        String response;

        if (pathSplit.length == 2) {
            response = gson.toJson(manager.getHistory());
            sendText(exchange, response);
        } else {
            sendNotFound(exchange);
        }
    }
}
