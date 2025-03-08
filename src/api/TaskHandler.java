package api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import com.google.gson.Gson;
import task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager manager;
    Gson gson;

    public TaskHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathSplit = exchange.getRequestURI().getPath().split("/");
        String requestMethod = exchange.getRequestMethod();

        switch (requestMethod) {
            case "GET":
                handleGetTasks(exchange, pathSplit);
                break;
            case "POST":
                handlePostTasks(exchange, pathSplit);
                break;
            case "DELETE":
                handleDeleteTasks(exchange, pathSplit);
                break;
            default:
                sendNotFound(exchange);
        }
    }

    private void handleGetTasks(HttpExchange exchange, String[] pathSplit) throws IOException {
        String response;

        if (pathSplit.length == 2) {
            response = gson.toJson(manager.getTaskList());
            sendText(exchange, response);
        } else if (pathSplit.length == 3) {
            try {
                Optional<Task> optTask = manager.getTaskById(Integer.parseInt(pathSplit[2]));
                if (optTask.isPresent()) {
                    response = gson.toJson(optTask.get());
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

    private void handlePostTasks(HttpExchange exchange, String[] pathSplit) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        if (pathSplit.length == 2) {
            Task newTask = gson.fromJson(body, Task.class);
            boolean notIntersects;
            if (newTask.getId() == 0) {
                notIntersects = manager.addTask(newTask);
            } else {
                notIntersects = manager.updateTask(newTask);
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

    private void handleDeleteTasks(HttpExchange exchange, String[] pathSplit) throws IOException {
        if (pathSplit.length == 3) {
            try {
                manager.deleteTaskById(Integer.parseInt(pathSplit[2]));
                sendConfirmation(exchange);
            } catch (NumberFormatException e) {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }
}
