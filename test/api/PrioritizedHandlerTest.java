package api;

import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrioritizedHandlerTest extends HttpTaskServerTest {

    public PrioritizedHandlerTest() throws IOException {
    }

    @Test
    public void shouldGetPrioritizedTasks() throws IOException, InterruptedException {
        manager.addTask(new Task("Task1", "Testing task1", TaskStatus.NEW,
                LocalDateTime.of(2000, 1, 1, 0, 0), Duration.ofMinutes(20)));
        manager.addTask(new Task("Task2", "Testing task2", TaskStatus.NEW));
        int id = manager.getCurrentId();
        manager.addEpic(new Epic("Epic1", "Testing epic1"));
        manager.addSubTask(new SubTask("Subtask1", "Testing subtask2", TaskStatus.NEW,
                LocalDateTime.of(2000, 2, 2, 10, 0), Duration.ofMinutes(30), id));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> prioritizedFromResponse = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertNotNull(prioritizedFromResponse, "Задачи не возвращаются");
        assertEquals(2, prioritizedFromResponse.size(), "Неверное количество задач");
        assertEquals("Task1", prioritizedFromResponse.getFirst().getName(),
                "Неверная задача в первом элементе");
        assertEquals("Subtask1", prioritizedFromResponse.get(1).getName(),
                "Неверная задача во втором элементе");
    }

    @Test
    public void shouldReturnNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized/1");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }
}