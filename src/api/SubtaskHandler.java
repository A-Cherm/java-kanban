package api;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.NotFoundException;
import exception.TimeIntersectionException;
import manager.TaskManager;
import task.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager manager;
    Gson gson;

    public SubtaskHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathSplit = exchange.getRequestURI().getPath().split("/");
        String requestMethod = exchange.getRequestMethod();

        try {
            switch (requestMethod) {
                case "GET":
                    handleGetSubtasks(exchange, pathSplit);
                    break;
                case "POST":
                    handlePostSubtasks(exchange, pathSplit);
                    break;
                case "DELETE":
                    handleDeleteSubtasks(exchange, pathSplit);
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (NumberFormatException | NotFoundException e) {
            sendNotFound(exchange);
        } catch (TimeIntersectionException e) {
            sendHasInteractions(exchange);
        }
    }

    private void handleGetSubtasks(HttpExchange exchange, String[] pathSplit) throws IOException {
        String response;

        if (pathSplit.length == 2) {
            response = gson.toJson(manager.getSubtaskList());
            sendText(exchange, response);
        } else if (pathSplit.length == 3) {
            response = gson.toJson(manager.getSubtaskById(Integer.parseInt(pathSplit[2])));
            sendText(exchange, response);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePostSubtasks(HttpExchange exchange, String[] pathSplit) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        if (pathSplit.length == 2) {
            Subtask newSubtask = gson.fromJson(body, Subtask.class);
            if (newSubtask.getId() == 0) {
                manager.addSubtask(newSubtask);
            } else {
                manager.updateSubtask(newSubtask);
            }
            sendConfirmation(exchange);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleDeleteSubtasks(HttpExchange exchange, String[] pathSplit) throws IOException {
        if (pathSplit.length == 3) {
            try {
                manager.deleteSubtaskById(Integer.parseInt(pathSplit[2]));
                sendConfirmation(exchange);
            } catch (NumberFormatException e) {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }
}