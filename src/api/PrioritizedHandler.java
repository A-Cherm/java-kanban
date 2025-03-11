package api;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager manager;
    Gson gson;

    public PrioritizedHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathSplit = exchange.getRequestURI().getPath().split("/");
        String requestMethod = exchange.getRequestMethod();

        if (requestMethod.equals("GET")) {
            handleGetPrioritizedTasks(exchange, pathSplit);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleGetPrioritizedTasks(HttpExchange exchange, String[] pathSplit) throws IOException {
        String response;

        if (pathSplit.length == 2) {
            response = gson.toJson(manager.getPrioritizedTasks());
            sendText(exchange, response);
        } else {
            sendNotFound(exchange);
        }
    }
}
