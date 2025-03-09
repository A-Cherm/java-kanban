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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SubtaskHandlerTest extends HttpTaskServerTest {

    public SubtaskHandlerTest() throws IOException {
    }

    @Test
    public void shouldGetSubtasks() throws IOException, InterruptedException {
        int id = manager.getCurrentId();
        manager.addEpic(new Epic("Epic1", "epic1"));
        manager.addSubtask(new Subtask("Subtask1", "Testing subtask1", TaskStatus.NEW, id));
        manager.addSubtask(new Subtask("Subtask2", "Testing subtask2", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(20), id));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> subtasksFromResponse = gson.fromJson(response.body(), new SubtaskListTypeToken().getType());

        assertNotNull(subtasksFromResponse, "Подзадачи не возвращаются");
        assertEquals(2, subtasksFromResponse.size(), "Неверное количество подзадач");
    }

    @Test
    public void shouldGetSubtaskById() throws IOException, InterruptedException {
        int id = manager.getCurrentId();
        manager.addEpic(new Epic("Epic1", "epic1"));
        manager.addSubtask(new Subtask("Subtask1", "Testing subtask1", TaskStatus.NEW, id));
        manager.addSubtask(new Subtask("Subtask2", "Testing subtask2", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(20), id));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + (id + 1));
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Subtask subtaskFromResponse = gson.fromJson(response.body(), Subtask.class);

        assertNotNull(subtaskFromResponse, "Подзадача не возвращается");
        assertEquals("Subtask1", subtaskFromResponse.getName(), "Неверное имя задачи");
        assertEquals("Testing subtask1", subtaskFromResponse.getDescription(),
                "Неверное описание задачи");
    }

    @Test
    public void shouldAddSubtask() throws IOException, InterruptedException {
        int id = manager.getCurrentId();
        manager.addEpic(new Epic("Epic1", "epic1"));
        Subtask subtask = new Subtask("Subtask1", "Testing subtask1", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(20), id);
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getSubtaskList();

        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Неверное количество подзадач");
        assertEquals("Subtask1", subtasksFromManager.getFirst().getName(), "Неверное имя подзадачи");
        assertEquals("Testing subtask1", subtasksFromManager.getFirst().getDescription(),
                "Неверное описание подзадачи");
    }

    @Test
    public void shouldNotAddSubtask() throws IOException, InterruptedException {
        int id = manager.getCurrentId();
        manager.addEpic(new Epic("Epic1", "epic1"));
        Subtask subtask1 = new Subtask("Subtask1", "Testing subtask1", TaskStatus.NEW,
                LocalDateTime.of(2000, 1, 1, 0, 0), Duration.ofMinutes(10), id);
        Subtask subtask2 = new Subtask("Subtask2", "Testing subtask2", TaskStatus.DONE,
                LocalDateTime.of(2000, 1, 1, 0, 0), Duration.ofMinutes(5), id);
        String subtaskJson1 = gson.toJson(subtask1);
        String subtaskJson2 = gson.toJson(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson1))
                .uri(url)
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson2))
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getSubtaskList();

        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Неверное количество подзадач");
        assertEquals("Subtask1", subtasksFromManager.getFirst().getName(), "Неверное имя подзадачи");
        assertEquals("Testing subtask1", subtasksFromManager.getFirst().getDescription(),
                "Неверное описание подзадачи");
    }

    @Test
    public void shouldUpdateSubtask() throws IOException, InterruptedException {
        int id = manager.getCurrentId();
        manager.addEpic(new Epic("Epic1", ""));
        manager.addSubtask(new Subtask("Subtask1", "Testing subtask1", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(5), id));
        Subtask updatedTask = new Subtask("Subtask2", "Testing subtask2", id + 1, TaskStatus.DONE,
                LocalDateTime.now(), Duration.ofMinutes(20), id);
        String updatedTaskJson = gson.toJson(updatedTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(updatedTaskJson))
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Subtask> tasksFromManager = manager.getSubtaskList();

        assertNotNull(tasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Неверное количество подзадач");
        assertEquals("Subtask2", tasksFromManager.getFirst().getName(), "Неверное имя подзадачи");
        assertEquals("Testing subtask2", tasksFromManager.getFirst().getDescription(),
                "Неверное описание подзадачи");
        assertEquals(20, tasksFromManager.getFirst().getDuration().toMinutes(),
                "Неверная длительность подзадачи");
    }

    @Test
    public void shouldNotUpdateInvalidSubtaskId() throws IOException, InterruptedException {
        int id = manager.getCurrentId();
        manager.addEpic(new Epic("Epic1", ""));
        manager.addSubtask(new Subtask("Subtask1", "Testing subtask1", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(5), id));
        Subtask updatedTask = new Subtask("Subtask2", "Testing subtask2", id + 2, TaskStatus.DONE,
                LocalDateTime.now(), Duration.ofMinutes(20), id);
        String updatedTaskJson = gson.toJson(updatedTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(updatedTaskJson))
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());

        List<Subtask> tasksFromManager = manager.getSubtaskList();

        assertNotNull(tasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Неверное количество поддзадач");
        assertEquals("Subtask1", tasksFromManager.getFirst().getName(), "Неверное имя подзадачи");
        assertEquals("Testing subtask1", tasksFromManager.getFirst().getDescription(),
                "Неверное описание подзадачи");
        assertEquals(5, tasksFromManager.getFirst().getDuration().toMinutes(),
                "Неверная длительность подзадачи");
    }

    @Test
    public void shouldDeleteSubtask() throws IOException, InterruptedException {
        int id = manager.getCurrentId();
        manager.addEpic(new Epic("Epic1", "epic1"));
        Subtask subtask = new Subtask("Subtask1", "Testing subtask1", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(5), id);
        manager.addTask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + (id + 1));
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getSubtaskList();

        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(0, subtasksFromManager.size(), "Неверное количество подзадач");
    }

    @Test
    public void shouldReturnNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/a");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }
}
