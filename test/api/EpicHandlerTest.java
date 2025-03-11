package api;

import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EpicHandlerTest extends HttpTaskServerTest {

    public EpicHandlerTest() throws IOException {
    }

    @Test
    public void shouldGetEpics() throws IOException, InterruptedException {
        manager.addEpic(new Epic("Epic1", "Testing epic1"));
        manager.addEpic(new Epic("Epic2", "Testing epic2"));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Epic> epicsFromResponse = gson.fromJson(response.body(), new EpicListTypeToken().getType());

        assertNotNull(epicsFromResponse, "Задачи не возвращаются");
        assertEquals(2, epicsFromResponse.size(), "Неверное количество задач");
    }

    @Test
    public void shouldGetEpicById() throws IOException, InterruptedException {
        int id = manager.getCurrentId();
        manager.addEpic(new Epic("Epic1", "Testing epic1"));
        manager.addEpic(new Epic("Epic2", "Testing epic2"));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Epic epicFromResponse = gson.fromJson(response.body(), Epic.class);

        assertNotNull(epicFromResponse, "Задача не возвращается");
        assertEquals("Epic1", epicFromResponse.getName(), "Неверное имя задачи");
        assertEquals("Testing epic1", epicFromResponse.getDescription(),
                "Неверное описание задачи");
    }

    @Test
    public void shouldGetEpicSubtasks() throws IOException, InterruptedException {
        int id = manager.getCurrentId();
        manager.addEpic(new Epic("Epic1", "Testing epic1"));
        manager.addEpic(new Epic("Epic2", "Testing epic2"));
        manager.addSubtask(new Subtask("Subtask1", "Testing subtask1", TaskStatus.NEW, id + 1));
        manager.addSubtask(new Subtask("Subtask2", "Testing subtask2", TaskStatus.DONE, id + 1));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + (id + 1) + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Subtask> subtasksFromResponse = gson.fromJson(response.body(), new SubtaskListTypeToken().getType());

        assertNotNull(subtasksFromResponse, "Подзадачи не возвращаются");
        assertEquals(2, subtasksFromResponse.size(), "Неверное число подзадач");
    }

    @Test
    public void shouldAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Testing epic1");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        int idFromResponse = Integer.parseInt(response.body());
        List<Epic> epicsFromManager = manager.getEpicList();

        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Неверное количество задач");
        assertEquals("Epic1", epicsFromManager.getFirst().getName(), "Неверное имя задачи");
        assertEquals("Testing epic1", epicsFromManager.getFirst().getDescription(),
                "Неверное описание задачи");
        assertEquals(1, idFromResponse, "Возвращается неверный id");
    }

    @Test
    public void shouldUpdateEpic() throws IOException, InterruptedException {
        manager.addEpic(new Epic("Epic1", "Testing epic1"));
        Epic newEpic = new Epic("Epic2", "Testing epic2", manager.getCurrentId() - 1);
        String epicJson = gson.toJson(newEpic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = manager.getEpicList();

        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Неверное количество задач");
        assertEquals("Epic2", epicsFromManager.getFirst().getName(), "Неверное имя задачи");
        assertEquals("Testing epic2", epicsFromManager.getFirst().getDescription(),
                "Неверное описание задачи");
    }

    @Test
    public void shouldDeleteEpic() throws IOException, InterruptedException {
        int id = manager.getCurrentId();
        Epic epic = new Epic("Epic1", "Testing Epic1");
        manager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = manager.getEpicList();

        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(0, epicsFromManager.size(), "Неверное количество задач");
    }

    @Test
    public void shouldReturnNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/a");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

}
