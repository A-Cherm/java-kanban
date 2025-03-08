package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import task.Task;
import task.TaskStatus;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private HttpServer httpServer;
    private Gson gson;

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
    }

    public void close() {
        httpServer.stop(1);
    }

    public Gson getGson() {
        return gson;
    }

    public static void main(String[] args) throws IOException {
        TaskManager manager1 = new InMemoryTaskManager();
        manager1.addTask(new Task("a", "b", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(10)));
        manager1.addTask(new Task("a", "b", TaskStatus.NEW));
        HttpTaskServer httpServer = new HttpTaskServer(manager1);

        httpServer.start();
    }
}
