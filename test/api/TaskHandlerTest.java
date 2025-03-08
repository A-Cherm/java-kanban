package api;

import org.junit.jupiter.api.Test;
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

class TaskHandlerTest extends HttpTaskServerTest {

    public TaskHandlerTest() throws IOException {
    }

    @Test
    public void shouldGetTasks() throws IOException, InterruptedException {
        manager.addTask(new Task("Task1", "Testing task1", TaskStatus.NEW));
        manager.addTask(new Task("Task2", "Testing task2", TaskStatus.NEW));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> tasksFromResponse = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertNotNull(tasksFromResponse, "Задачи не возвращаются");
        assertEquals(2, tasksFromResponse.size(), "Неверное количество задач");
    }

    @Test
    public void shouldGetTaskById() throws IOException, InterruptedException {
        int id = manager.getCurrentId();
        manager.addTask(new Task("Task1", "Testing task1", TaskStatus.NEW));
        manager.addTask(new Task("Task2", "Testing task2", TaskStatus.NEW));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task taskFromResponse = gson.fromJson(response.body(), Task.class);

        assertNotNull(taskFromResponse, "Задача не возвращается");
        assertEquals("Task1", taskFromResponse.getName(), "Неверное имя задачи");
        assertEquals("Testing task1", taskFromResponse.getDescription(),
                "Неверное описание задачи");
    }

    @Test
    public void shouldAddTask() throws IOException, InterruptedException {
        Task task = new Task("Task1", "Testing task1", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTaskList();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Неверное количество задач");
        assertEquals("Task1", tasksFromManager.getFirst().getName(), "Неверное имя задачи");
        assertEquals("Testing task1", tasksFromManager.getFirst().getDescription(),
                "Неверное описание задачи");
    }

    @Test
    public void shouldNotAddTask() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "Testing task1", TaskStatus.NEW,
                LocalDateTime.of(2000, 1, 1, 0, 0), Duration.ofMinutes(10));
        Task task2 = new Task("Task2", "Testing task2", TaskStatus.DONE,
                LocalDateTime.of(2000, 1, 1, 0, 0), Duration.ofMinutes(5));
        String taskJson1 = gson.toJson(task1);
        String taskJson2 = gson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                .uri(url)
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson2))
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());

        List<Task> tasksFromManager = manager.getTaskList();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Неверное количество задач");
        assertEquals("Task1", tasksFromManager.getFirst().getName(), "Неверное имя задачи");
        assertEquals("Testing task1", tasksFromManager.getFirst().getDescription(),
                "Неверное описание задачи");
    }

    @Test
    public void shouldUpdateTask() throws IOException, InterruptedException {
        int id = manager.getCurrentId();
        manager.addTask(new Task("Task1", "Testing task1", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(5)));
        Task updatedTask = new Task("Task2", "Testing task2", id, TaskStatus.DONE,
                LocalDateTime.now(), Duration.ofMinutes(20));
        String updatedTaskJson = gson.toJson(updatedTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(updatedTaskJson))
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTaskList();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Неверное количество задач");
        assertEquals("Task2", tasksFromManager.getFirst().getName(), "Неверное имя задачи");
        assertEquals("Testing task2", tasksFromManager.getFirst().getDescription(),
                "Неверное описание задачи");
        assertEquals(20, tasksFromManager.getFirst().getDuration().toMinutes(),
                "Неверная длительность задачи");
    }

    @Test
    public void shouldDeleteTask() throws IOException, InterruptedException {
        int id = manager.getCurrentId();
        Task task = new Task("Task1", "Testing task1", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(5));
        manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTaskList();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(0, tasksFromManager.size(), "Неверное количество задач");
    }

    @Test
    public void shouldReturnNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/a");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }
}