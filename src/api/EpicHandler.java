package api;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.NotFoundException;
import manager.TaskManager;
import task.Epic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager manager;
    Gson gson;

    public EpicHandler(TaskManager manager, Gson gson) {
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
                    handleGetEpics(exchange, pathSplit);
                    break;
                case "POST":
                    handlePostEpics(exchange, pathSplit);
                    break;
                case "DELETE":
                    handleDeleteEpics(exchange, pathSplit);
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (NumberFormatException | NotFoundException e) {
            sendNotFound(exchange);
        }
    }

    private void handleGetEpics(HttpExchange exchange, String[] pathSplit) throws IOException {
        String response;

        if (pathSplit.length == 2) {
            response = gson.toJson(manager.getEpicList());
            sendText(exchange, response);
        } else {
            int epicId = Integer.parseInt(pathSplit[2]);
            Epic epic = manager.getEpicById(epicId);

            if (pathSplit.length == 3) {
                response = gson.toJson(epic);
                sendText(exchange, response);
            } else if (pathSplit.length == 4 && pathSplit[3].equals("subtasks")) {
                response = gson.toJson(manager.getEpicSubtaskList(epicId));
                sendText(exchange, response);
            } else {
                sendNotFound(exchange);
            }
        }
    }

    private void handlePostEpics(HttpExchange exchange, String[] pathSplit) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        if (pathSplit.length == 2) {
            Epic newEpic = gson.fromJson(body, Epic.class);
            if (newEpic.getId() == 0) {
                manager.addEpic(newEpic);
                sendText(exchange, gson.toJson(manager.getCurrentId() - 1));
            } else {
                manager.updateEpic(newEpic);
                sendConfirmation(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleDeleteEpics(HttpExchange exchange, String[] pathSplit) throws IOException {
        if (pathSplit.length == 3) {
            manager.deleteEpicById(Integer.parseInt(pathSplit[2]));
            sendConfirmation(exchange);
        } else {
            sendNotFound(exchange);
        }
    }
}