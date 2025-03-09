package api;

import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryHandlerTest extends HttpTaskServerTest {

    public HistoryHandlerTest() throws IOException {
    }

    @Test
    public void shouldGetHistory() throws IOException, InterruptedException {
        manager.addTask(new Task("Task1", "Testing task1", TaskStatus.NEW));
        manager.addTask(new Task("Task2", "Testing task2", TaskStatus.NEW));
        int id = manager.getCurrentId();
        manager.addEpic(new Epic("Epic1", "Testing epic1"));
        manager.addSubtask(new Subtask("Subtask1", "Testing subtask2", TaskStatus.NEW, id));

        manager.getTaskById(id - 1);
        manager.getTaskById(id - 2);
        manager.getSubtaskById(id + 1);
        manager.getEpicById(id);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> historyFromResponse = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertNotNull(historyFromResponse, "Задачи не возвращаются");
        assertEquals(4, historyFromResponse.size(), "Неверное количество задач");
    }

    @Test
    public void shouldReturnNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history/1");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }
}