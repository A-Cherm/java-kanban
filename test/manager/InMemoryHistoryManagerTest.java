package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Task;
import task.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager historyManager;

    @BeforeEach
    public void newHistoryManager() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    public void shouldAddTask() {
        List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не возвращается");
        assertEquals(0, history.size(), "Неверный размер истории");

        Task task = new Task("a", "b", 1, TaskStatus.DONE);
        historyManager.addTask(task);
        history = historyManager.getHistory();

        assertNotNull(history, "История не возвращается");
        assertEquals(1, history.size(), "Неверный размер истории");

        historyManager.addTask(new Epic("a", "b", 2));
        historyManager.addTask(new Epic("a", "b", 3));

        history = historyManager.getHistory();

        assertNotNull(history, "История не возвращается");
        assertEquals(3, history.size(), "Неверный размер истории");

        historyManager.addTask(task);

        assertEquals(2, historyManager.getListHead().data.getId(), "Неправильный элемент в голове списка");
        assertEquals(1, historyManager.getListTail().data.getId(), "Неправильный элемент в хвосте списка");

        history = historyManager.getHistory();

        assertNotNull(history, "История не возвращается");
        assertEquals(3, history.size(), "Неверный размер истории");
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

        Task task1 = new Task("b", "c", 2, TaskStatus.DONE);
        Task task2 = new Task("b", "c", 3, TaskStatus.DONE);
        historyManager.linkLast(task1);
        Node<Task> node2 = historyManager.linkLast(task2);

        historyManager.removeNode(node2);

        assertNotNull(historyManager.getListHead(), "Неверная ссылка на голову списка");
        assertNotNull(historyManager.getListTail(), "Неверная ссылка на хвост списка");
        assertEquals(historyManager.getListHead(), historyManager.getListTail(),
                "Не совпадают ссылки на голову и хвост");
        assertNull(historyManager.getListTail().next, "Непустая ссылка на следующую ноду хвоста списка");
        assertNull(historyManager.getListHead().prev, "Непустая ссылка на предыдущую ноду головы списка");
    }

    @Test
    public void shouldRemoveTask() {
        historyManager.addTask(new Task("a", "b", 1, TaskStatus.DONE));
        historyManager.addTask(new Task("a", "b", 2, TaskStatus.DONE));
        historyManager.addTask(new Task("a", "b", 3, TaskStatus.DONE));

        List<Task> history = historyManager.getHistory();

        assertEquals(3, history.size(), "Неверный размер истории");

        historyManager.remove(1);
        history = historyManager.getHistory();

        assertEquals(2, history.size(), "Неверный размер истории");
        assertEquals(2, history.getFirst().getId(), "Неверный первый элемент");

        historyManager.addTask(new Task("a", "b", 1, TaskStatus.DONE));
        historyManager.remove(3);
        history = historyManager.getHistory();

        assertEquals(2, history.size(), "Неверный размер истории");
        assertEquals(2, history.getFirst().getId(), "Неверный первый элемент");
        assertEquals(1, history.getLast().getId(), "Неверный последний элемент");

        historyManager.addTask(new Task("a", "b", 3, TaskStatus.DONE));
        historyManager.remove(3);
        history = historyManager.getHistory();

        assertEquals(2, history.size(), "Неверный размер истории");
        assertEquals(2, history.getFirst().getId(), "Неверный первый элемент");
        assertEquals(1, history.getLast().getId(), "Неверный последний элемент");
    }
}