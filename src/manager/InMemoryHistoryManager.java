package manager;

import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private Node<Task> listHead;
    private Node<Task> listTail;
    private final Map<Integer, Node<Task>> taskHistory;

    public InMemoryHistoryManager() {
        taskHistory = new HashMap<>();
        listHead = null;
        listTail = null;
    }

    @Override
    public void addTask(Task task) {
        if (taskHistory.containsKey(task.getId())) {
            removeNode(taskHistory.get(task.getId()));
        }
        Node<Task> newNode = linkLast(task);
        taskHistory.put(task.getId(), newNode);
    }

    @Override
    public void remove(int id) {
        if (taskHistory.containsKey(id)) {
            removeNode(taskHistory.get(id));
            taskHistory.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    public Node<Task> linkLast(Task task) {
        Node<Task> newNode = new Node<>(task);
        newNode.prev = listTail;
        if (listTail == null) {
            listHead = newNode;
        } else {
            listTail.next = newNode;
        }
        listTail = newNode;
        return newNode;
    }

    public List<Task> getTasks() {
        List<Task> taskList = new ArrayList<>();
        Node<Task> node = listHead;

        while (node != null) {
            taskList.add(node.data);
            node = node.next;
        }
        return taskList;
    }

    public void removeNode(Node<Task> node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            listHead = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            listTail = node.prev;
        }
    }

    public Node<Task> getListHead() {
        return listHead;
    }

    public Node<Task> getListTail() {
        return listTail;
    }
}
