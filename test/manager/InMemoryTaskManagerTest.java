package manager;

import exception.TimeIntersectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void newTaskManager() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void shouldCheckTimeIntersections() {
        taskManager.addTask(new Task("a", "a", TaskStatus.NEW,
                LocalDateTime.of(2000, 1, 1, 10, 0), Duration.ofMinutes(30)));
        taskManager.addTask(new Task("a", "a", TaskStatus.NEW,
                LocalDateTime.of(2000, 1, 1, 10, 50), Duration.ofMinutes(20)));

        assertEquals(2, taskManager.getTasks().size(), "Неверное число задач");

        try {
            taskManager.addTask(new Task("a", "a", TaskStatus.NEW,
                    LocalDateTime.of(2000, 1, 1, 10, 20), Duration.ofMinutes(5)));
        } catch (TimeIntersectionException ignored) {
        }

        assertEquals(2, taskManager.getTasks().size(), "Неверное число задач");

        try {
            taskManager.addTask(new Task("a", "a", TaskStatus.NEW,
                    LocalDateTime.of(2000, 1, 1, 10, 20), Duration.ofMinutes(40)));
        } catch (TimeIntersectionException ignored) {
        }

        assertEquals(2, taskManager.getTasks().size(), "Неверное число задач");

        try {
            taskManager.addTask(new Task("a", "a", TaskStatus.NEW,
                    LocalDateTime.of(2000, 1, 1, 10, 40), Duration.ofMinutes(60)));
        } catch (TimeIntersectionException ignored) {
        }

        assertEquals(2, taskManager.getTasks().size(), "Неверное число задач");

        taskManager.addTask(new Task("a", "a", TaskStatus.NEW,
                LocalDateTime.of(2000, 1, 1, 10, 40), Duration.ofMinutes(10)));

        assertEquals(3, taskManager.getTasks().size(), "Неверное число задач");
    }
}