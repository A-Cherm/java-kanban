package api;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import task.SubTask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

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
    }

    private void handleGetSubtasks(HttpExchange exchange, String[] pathSplit) throws IOException {
        String response;

        if (pathSplit.length == 2) {
            response = gson.toJson(manager.getSubTaskList());
            sendText(exchange, response);
        } else if (pathSplit.length == 3) {
            try {
                Optional<SubTask> optSubtask = manager.getSubTaskById(Integer.parseInt(pathSplit[2]));
                if (optSubtask.isPresent()) {
                    response = gson.toJson(optSubtask.get());
                    sendText(exchange, response);
                } else {
                    sendNotFound(exchange);
                }
            } catch (NumberFormatException e) {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePostSubtasks(HttpExchange exchange, String[] pathSplit) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        if (pathSplit.length == 2) {
            SubTask newSubtask = gson.fromJson(body, SubTask.class);
            boolean notIntersects;
            if (newSubtask.getId() == 0) {
                notIntersects = manager.addSubTask(newSubtask);
            } else {
                notIntersects = manager.updateSubTask(newSubtask);
            }
            if (notIntersects) {
                sendConfirmation(exchange);
            } else {
                sendHasInteractions(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleDeleteSubtasks(HttpExchange exchange, String[] pathSplit) throws IOException {
        if (pathSplit.length == 3) {
            try {
                manager.deleteSubTaskById(Integer.parseInt(pathSplit[2]));
                sendConfirmation(exchange);
            } catch (NumberFormatException e) {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }
}