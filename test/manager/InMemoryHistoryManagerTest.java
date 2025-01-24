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
        Task task = new Task("a", "b", 1, TaskStatus.DONE);
        historyManager.addTask(task);
        ArrayList<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не возвращается");
        assertEquals(1, history.size(), "Неверный размер истории");

        Epic epic;
        for (int i = 0; i < 9; i++) {
            epic = new Epic("c", "d", i + 2);
            historyManager.addTask(epic);
        }
        history = historyManager.getHistory();

        assertNotNull(history, "История не возвращается");
        assertEquals(10, history.size(), "Неверный размер истории");

        historyManager.addTask(task);

        assertEquals(2, historyManager.getListHead().data.getId(), "Неправильный элемент в голове списка");
        assertEquals(1, historyManager.getListTail().data.getId(), "Неправильный элемент в хвосте списка");

        history = historyManager.getHistory();

        assertNotNull(history, "История не возвращается");
        assertEquals(10, history.size(), "Неверный размер истории");
    }

    @Test
    public void shouldRemoveNode() {
        Task task = new Task("a", "b", 1, TaskStatus.DONE);
        Node<Task> node = historyManager.linkLast(task);

        assertNotNull(historyManager.getListHead(), "Неверная ссылка на голову списка");
        assertNotNull(historyManager.getListTail(), "Неверная ссылка на хвост списка");

        historyManager.removeNode(node);

        assertNull(historyManager.getListHead(), "Неверная ссылка на голову списка");
        assertNull(historyManager.getListTail(), "Неверная ссылка на хвост списка");
    }
}