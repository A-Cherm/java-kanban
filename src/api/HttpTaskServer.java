package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer httpServer;
    private final Gson gson;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .create();
        this.httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        this.httpServer.createContext("/tasks", new TaskHandler(manager, gson));
        this.httpServer.createContext("/epics", new EpicHandler(manager, gson));
        this.httpServer.createContext("/subtasks", new SubtaskHandler(manager, gson));
        this.httpServer.createContext("/history", new HistoryHandler(manager, gson));
        this.httpServer.createContext("/prioritized", new PrioritizedHandler(manager, gson));
    }

    public void start() {
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту.");
    }

    public void close() {
        httpServer.stop(1);
        System.out.println("HTTP-сервер на " + PORT + " порту остановлен.");
    }

    public Gson getGson() {
        return gson;
    }

    public static void main(String[] args) throws IOException {
        TaskManager manager1 = new InMemoryTaskManager();
        manager1.addTask(new Task("Task1", "a", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(10)));
        manager1.addTask(new Task("Task2", "b", TaskStatus.NEW));
        manager1.addEpic(new Epic("Epic1", "c"));
        manager1.addSubtask(new Subtask("Subtask1", "d", TaskStatus.NEW, 3));

        manager1.getTaskById(2);
        manager1.getSubtaskById(4);
        manager1.getEpicById(3);

        Epic newEpic = new Epic("Epic2", "e");
        HttpTaskServer httpServer = new HttpTaskServer(manager1);

        System.out.println(httpServer.getGson().toJson(newEpic));
        httpServer.start();
    }
}
