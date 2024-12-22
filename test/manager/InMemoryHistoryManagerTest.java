package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Task;
import task.TaskStatus;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager historyManager;

    @BeforeEach
    public void newHistoryManager() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    public void shouldAddTask() {
        Task task = new Task("a", "b", TaskStatus.DONE);
        historyManager.addTask(task);
        ArrayList<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не возвращается");
        assertEquals(1, history.size(), "Неверный размер истории");

        Epic epic;
        for (int i = 0; i < InMemoryHistoryManager.HISTORY_SIZE; i++) {
            epic = new Epic("c", "d", i + 2);
            historyManager.addTask(epic);
        }
        history = historyManager.getHistory();

        assertNotNull(history, "История не возвращается");
        assertEquals(InMemoryHistoryManager.HISTORY_SIZE, history.size(), "Неверный размер истории");
        assertEquals(2, history.getFirst().getId());
    }
}