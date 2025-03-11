import manager.InMemoryTaskManager;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        // тесты работы методов
        // добавление задач, эпиков, подзадач
        Task newTask = new Task("Задача 1", "123", TaskStatus.NEW);
        taskManager.addTask(newTask);
        newTask = new Task("Задача 2", "321", TaskStatus.DONE);
        taskManager.addTask(newTask);
        Epic newEpic = new Epic("Эпик 1", "");
        taskManager.addEpic(newEpic);
        Subtask newSubtask = new Subtask("Подзадача 1", "", TaskStatus.IN_PROGRESS, 3);
        taskManager.addSubtask(newSubtask);
        newSubtask = new Subtask("Подзадача 2", "", TaskStatus.NEW, 3);
        taskManager.addSubtask(newSubtask);
        newEpic = new Epic("Эпик 2", "");
        taskManager.addEpic(newEpic);
        newSubtask = new Subtask("Подзадача 3", "", TaskStatus.DONE, 6);
        taskManager.addSubtask(newSubtask);

        System.out.println("Добавление");
        InMemoryTaskManager.printAllTasks(taskManager);

        // обновление задач
        newTask = new Task("Задача 1", "111", 1, TaskStatus.IN_PROGRESS);
        taskManager.updateTask(newTask);
        newSubtask = new Subtask("Подзадача 1", "", 4, TaskStatus.DONE, 3);
        taskManager.updateSubtask(newSubtask);
        newSubtask = new Subtask("Подзадача 2", "", 5, TaskStatus.DONE, 3);
        taskManager.updateSubtask(newSubtask);
        newEpic = new Epic("Эпик 2", "новый!",3);
        taskManager.updateEpic(newEpic);

        System.out.println("Обновление");
        InMemoryTaskManager.printAllTasks(taskManager);

        // удаление
        taskManager.deleteTaskById(1);
        taskManager.deleteTaskById(10);
        taskManager.deleteSubtaskById(4);
        taskManager.deleteSubtaskById(7);

        System.out.println("Удаление");
        InMemoryTaskManager.printAllTasks(taskManager);

        taskManager.deleteAllEpics();

        InMemoryTaskManager.printAllTasks(taskManager);

        // удаление всех подзадач
        taskManager.deleteEpicById(8);
        taskManager.deleteAllTasks();
        int id = taskManager.getCurrentId();
        newEpic = new Epic("Эпик 1", "");
        taskManager.addEpic(newEpic);
        newSubtask = new Subtask("Подзадача 1", "", TaskStatus.IN_PROGRESS, id);
        taskManager.addSubtask(newSubtask);
        newSubtask = new Subtask("Подзадача 2", "", TaskStatus.NEW, id);
        taskManager.addSubtask(newSubtask);
        newSubtask = new Subtask("Подзадача 3", "", TaskStatus.NEW, id);
        taskManager.addSubtask(newSubtask);

        System.out.println("Удаление всех подзадач");
        InMemoryTaskManager.printAllTasks(taskManager);

        taskManager.deleteAllSubtasks();

        InMemoryTaskManager.printAllTasks(taskManager);

        newSubtask = new Subtask("Подзадача 2", "", TaskStatus.DONE, id);
        taskManager.addSubtask(newSubtask);
        newSubtask = new Subtask("Подзадача 3", "", TaskStatus.DONE, id);
        taskManager.addSubtask(newSubtask);

        InMemoryTaskManager.printAllTasks(taskManager);

        // история
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();

        newTask = new Task("Задача 1", "123", TaskStatus.NEW);
        taskManager.addTask(newTask);
        newTask = new Task("Задача 2", "321", TaskStatus.DONE);
        taskManager.addTask(newTask);

        id = taskManager.getCurrentId();
        newEpic = new Epic("Эпик 1", "");
        taskManager.addEpic(newEpic);
        newSubtask = new Subtask("Подзадача 1", "", TaskStatus.IN_PROGRESS, id);
        taskManager.addSubtask(newSubtask);
        newSubtask = new Subtask("Подзадача 2", "", TaskStatus.NEW, id);
        taskManager.addSubtask(newSubtask);
        newSubtask = new Subtask("Подзадача 3", "", TaskStatus.NEW, id);
        taskManager.addSubtask(newSubtask);
        newEpic = new Epic("Эпик 2", "");
        taskManager.addEpic(newEpic);
        InMemoryTaskManager.printAllTasks(taskManager);

        taskManager.getTaskById(id - 2);
        taskManager.getTaskById(id - 2);

        InMemoryTaskManager.printHistory(taskManager);

        taskManager.getTaskById(id - 1);
        taskManager.deleteTaskById(id - 1);

        InMemoryTaskManager.printHistory(taskManager);

        taskManager.getSubtaskById(id + 1);
        taskManager.getSubtaskById(id + 3);
        taskManager.getSubtaskById(id + 2);

        InMemoryTaskManager.printHistory(taskManager);

        taskManager.getTaskById(id - 2);
        taskManager.getSubtaskById(id + 1);
        taskManager.getEpicById(id + 4);
        taskManager.getEpicById(id + 4);

        InMemoryTaskManager.printHistory(taskManager);

        taskManager.deleteTaskById(id - 2);

        InMemoryTaskManager.printHistory(taskManager);

        taskManager.deleteEpicById(id);

        InMemoryTaskManager.printHistory(taskManager);

        //приоритет
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();

        newTask = new Task("Задача 1", "123", 1, TaskStatus.NEW,
                LocalDateTime.of(1,1,1,1,1), Duration.ofMinutes(10));
        taskManager.addTask(newTask);
        newTask = new Task("Задача 2", "321", 1, TaskStatus.DONE,
                LocalDateTime.of(1,2,1,1,1), Duration.ofMinutes(20));
        taskManager.addTask(newTask);

        id = taskManager.getCurrentId();
        newEpic = new Epic("Эпик 1", "");
        taskManager.addEpic(newEpic);
        newSubtask = new Subtask("Подзадача 1", "", 1, TaskStatus.IN_PROGRESS,
                LocalDateTime.of(1,4,1,1,1), Duration.ofMinutes(20), id);
        taskManager.addSubtask(newSubtask);
        newSubtask = new Subtask("Подзадача 2", "", 1, TaskStatus.NEW,
                LocalDateTime.of(1,3,1,1,1), Duration.ofMinutes(5), id);
        taskManager.addSubtask(newSubtask);
        newSubtask = new Subtask("Подзадача 3", "", 1, TaskStatus.NEW,
                LocalDateTime.of(2,1,1,1,15), Duration.ofMinutes(15), id);
        taskManager.addSubtask(newSubtask);
        newEpic = new Epic("Эпик 2", "");
        taskManager.addEpic(newEpic);
        InMemoryTaskManager.printAllTasks(taskManager);
        InMemoryTaskManager.printPrioritizedTasks(taskManager);

        taskManager.updateTask(new Task("b", "c", id - 2, TaskStatus.DONE));
        InMemoryTaskManager.printPrioritizedTasks(taskManager);

        taskManager.updateTask(new Task("b", "c", id - 2, TaskStatus.DONE,
                LocalDateTime.of(1,1,1,1,1), Duration.ofMinutes(10)));
        InMemoryTaskManager.printPrioritizedTasks(taskManager);

        taskManager.updateTask(new Task("b", "c", id - 2, TaskStatus.DONE,
                LocalDateTime.of(1,1,1,1,1), Duration.ofMinutes(5)));
        InMemoryTaskManager.printPrioritizedTasks(taskManager);

        taskManager.updateTask(new Task("b", "c", id - 2, TaskStatus.DONE,
                LocalDateTime.of(1,1,1,1,1), Duration.ofMinutes(10)));
        InMemoryTaskManager.printPrioritizedTasks(taskManager);

        taskManager.deleteAllTasks();
        InMemoryTaskManager.printPrioritizedTasks(taskManager);
    }
}