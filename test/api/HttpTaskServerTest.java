package api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.InMemoryTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import task.Epic;
import task.SubTask;
import task.Task;

import java.io.IOException;
import java.util.List;

public abstract class HttpTaskServerTest {
    InMemoryTaskManager manager = new InMemoryTaskManager();
    HttpTaskServer httpTaskServer = new HttpTaskServer(manager);
    Gson gson = httpTaskServer.getGson();

    public HttpTaskServerTest() throws IOException {
    }

    protected static class TaskListTypeToken extends TypeToken<List<Task>> {
    }

    protected static class EpicListTypeToken extends TypeToken<List<Epic>> {
    }

    protected static class SubTaskListTypeToken extends TypeToken<List<SubTask>> {
    }

    @BeforeEach
    protected void setUp() {
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        httpTaskServer.start();
    }

    @AfterEach
    protected void shutDown() {
        httpTaskServer.close();
    }
}