package manager;

import exception.NotFoundException;
import exception.TimeIntersectionException;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected TaskManager taskManager;

    @Test
    public void shouldAddTaskAndReturn() {
        Task task1 = new Task("a", "b", 1, TaskStatus.NEW,
                LocalDateTime.of(2000,1,1, 10,10), Duration.ofMinutes(30));
        taskManager.addTask(task1);

        Task task2 = taskManager.getTaskById(1);

        assertNotNull(task2, "Задача не найдена");
        assertEquals(task1, task2, "Задачи не совпадают");
        assertEquals(task1.getName(), task2.getName(), "Имя задач не совпадает");
        assertEquals(task1.getDescription(), task2.getDescription(), "Описание задач не совпадает");
        assertEquals(task1.getStatus(), task2.getStatus(), "Статус задач не совпадает");
        assertEquals(task1.getStartTime(), task2.getStartTime(), "Время начала задач не совпадает");
        assertEquals(task1.getDuration(), task2.getDuration(), "Длительность задач не совпадает");
        assertEquals(LocalDateTime.of(2000, 1, 1, 10, 40), task1.getEndTime(),
                "Неверное время окончания задачи");
        assertEquals(LocalDateTime.of(2000, 1, 1, 10, 40), task2.getEndTime(),
                "Неверное время окончания задачи");

        List<Task> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Список задач не возвращается");
        assertEquals(1, tasks.size(), "Неверное количество задач");
        assertEquals(task1, tasks.getFirst(), "Задачи не совпадают");
    }

    @Test
    public void shouldAddEpicAndReturn() {
        Epic epic1 = new Epic("a", "a");

        taskManager.addEpic(epic1);

        Epic epic2 = taskManager.getEpicById(1);

        assertNotNull(epic2, "Эпик не найден");
        assertEquals(epic1, epic2, "Эпики не совпадают");
        assertEquals(epic1.getName(), epic2.getName(), "Имя эпиков не совпадает");
        assertEquals(epic1.getDescription(), epic2.getDescription(), "Описание эпиков не совпадает");

        List<Epic> epics = taskManager.getEpicList();

        assertNotNull(epics, "Список эпиков не возвращается");
        assertEquals(1, epics.size(), "Неверное количество эпиков");
        assertEquals(epic1, epics.getFirst(), "Эпики не совпадают");
    }

    @Test
    public void shouldAddSubtaskAndReturn() {
        Epic epic = new Epic("a", "a");
        Subtask subTask1 = new Subtask("a", "b", TaskStatus.NEW,
                LocalDateTime.of(2000, 1, 1, 20, 0), Duration.ofMinutes(20), 1);

        taskManager.addEpic(epic);
        taskManager.addSubtask(subTask1);

        Subtask subTask2 = taskManager.getSubtaskById(2);

        assertNotNull(subTask2, "Подзадача не найдена");
        assertEquals(subTask1, subTask2, "Подзадачи не совпадают");
        assertEquals(subTask1.getName(), subTask2.getName(), "Имя подзадач не совпадает");
        assertEquals(subTask1.getDescription(), subTask2.getDescription(), "Описание подзадач не совпадает");
        assertEquals(subTask1.getStatus(), subTask2.getStatus(), "Статус подзадач не совпадает");
        assertEquals(subTask1.getStartTime(), subTask2.getStartTime(), "Время начала задач не совпадает");
        assertEquals(subTask1.getDuration(), subTask2.getDuration(), "Длительность задач не совпадает");
        assertEquals(LocalDateTime.of(2000, 1, 1, 20, 20), subTask1.getEndTime(),
                "Неверное время окончания задачи");
        assertEquals(LocalDateTime.of(2000, 1, 1, 20, 20), subTask2.getEndTime(),
                "Неверное время окончания задачи");

        List<Subtask> subTasks = taskManager.getSubtaskList();

        assertNotNull(subTasks, "Список подзадач не возвращается");
        assertEquals(1, subTasks.size(), "Неверное количество подзадач");
        assertEquals(subTask1, subTasks.getFirst(), "Подзадачи не совпадают");
    }

    @Test
    public void shouldAddTaskWithCorrectId() {
        Task task1 = new Task("a", "b", 5, TaskStatus.NEW);
        taskManager.addTask(task1);

        Task task2 = taskManager.getTaskById(1);

        assertNotNull(task2, "Задача не найдена");
        assertEquals(task1, task2, "Задачи не совпадают");
        assertEquals(1, task2.getId(), "Неверный индекс задачи");
    }

    @Test
    public void shouldUpdateEpicStatus() {
        Epic epic = new Epic("a", "a");
        Subtask subTask1 = new Subtask("a", "b", TaskStatus.NEW,
                LocalDateTime.of(2000,1,1,20,0), Duration.ofMinutes(20), 1);

        taskManager.addEpic(epic);
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Неверный статус пустого эпика");
        assertNull(epic.getStartTime(), "Неверное время старта пустого эпика");
        assertEquals(Duration.ZERO, epic.getDuration(), "Неверная длительность пустого эпика");
        assertNull(epic.getEndTime(), "Неверное время конца пустого эпика");

        taskManager.addSubtask(subTask1);
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Неверный статус эпика");
        assertEquals(LocalDateTime.of(2000,1,1,20,0), epic.getStartTime(),
                "Неверное время старта эпика");
        assertEquals(Duration.ofMinutes(20), epic.getDuration(), "Неверная длительность эпика");
        assertEquals(LocalDateTime.of(2000,1,1,20,20), epic.getEndTime(),
                "Неверное время конца эпика");

        Subtask subTask2 = new Subtask("a", "b", 2, TaskStatus.DONE, 1);

        taskManager.updateSubtask(subTask2);
        assertEquals(TaskStatus.DONE, epic.getStatus(), "Неверный статус эпика");

        taskManager.addEpic(new Epic("b", "b"));
        taskManager.addSubtask(new Subtask("a", "b", TaskStatus.NEW, 3));
        taskManager.addSubtask(new Subtask("a", "b", TaskStatus.DONE, 3));

        epic = taskManager.getEpicById(3);
        assertNotNull(epic, "Эпик не найден");
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Неверный статус эпика");

        taskManager.updateSubtask(new Subtask("a", "b", 4, TaskStatus.IN_PROGRESS, 3));
        taskManager.updateSubtask(new Subtask("a", "b", 5, TaskStatus.IN_PROGRESS, 3));

        epic = taskManager.getEpicById(3);
        assertNotNull(epic, "Эпик не найден");
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Неверный статус эпика");
    }

    @Test
    public void shouldReturnEpicSubtaskList() {
        Epic epic = new Epic("a", "a");
        Subtask subTask = new Subtask("a", "b", TaskStatus.NEW, 1);

        taskManager.addEpic(epic);
        taskManager.addSubtask(subTask);

        List<Subtask> epicSubtaskList = taskManager.getEpicSubtaskList(1);

        assertNotNull(epicSubtaskList, "Список подзадач эпика не возвращается");
        assertEquals(1, epicSubtaskList.size(), "Неверное количество подзадач");
        assertEquals(subTask, epicSubtaskList.getFirst(), "Подзадачи не совпадают");
    }

    @Test
    public void shouldDeleteTaskById() {
        Task task = new Task("a", "b", TaskStatus.NEW);
        taskManager.addTask(task);
        taskManager.getTaskById(1);

        List<Task> history = taskManager.getHistory();

        assertNotNull(history, "История задач не возвращается");
        assertEquals(1, history.size(), "Неверный размер истории");

        taskManager.deleteTaskById(1);

        List<Task> tasks = taskManager.getTaskList();
        history = taskManager.getHistory();

        assertNotNull(tasks, "Список задач не возвращается");
        assertEquals(0, tasks.size(), "Неверное количество задач");
        assertNotNull(history, "История задач не возвращается");
        assertEquals(0, history.size(), "Неверный размер истории");
    }

    @Test
    public void shouldDeleteEpicById() {
        Epic epic = new Epic("a", "a");
        Subtask subTask = new Subtask("a", "b", TaskStatus.NEW, 1);

        taskManager.addEpic(epic);
        taskManager.addSubtask(subTask);
        taskManager.getEpicById(1);
        taskManager.getSubtaskById(2);

        List<Task> history = taskManager.getHistory();

        assertNotNull(history, "История задач не возвращается");
        assertEquals(2, history.size(), "Неверный размер истории");

        taskManager.deleteEpicById(1);

        List<Epic> epics = taskManager.getEpicList();
        List<Subtask> subTasks = taskManager.getSubtaskList();
        history = taskManager.getHistory();

        assertNotNull(epics, "Список эпиков не возвращается");
        assertEquals(0, epics.size(), "Неверное количество эпиков");
        assertNotNull(subTasks, "Список подзадач не возвращается");
        assertEquals(0, subTasks.size(), "Неверное количество подзадач");
        assertNotNull(history, "История задач не возвращается");
        assertEquals(0, history.size(), "Неверный размер истории");
    }

    @Test
    public void shouldDeleteSubtaskById() {
        Epic epic = new Epic("a", "a");
        Subtask subTask = new Subtask("a", "b", TaskStatus.NEW, 1);

        taskManager.addEpic(epic);
        taskManager.addSubtask(subTask);
        taskManager.getSubtaskById(2);

        List<Task> history = taskManager.getHistory();

        assertNotNull(history, "История задач не возвращается");
        assertEquals(1, history.size(), "Неверный размер истории");

        taskManager.deleteSubtaskById(2);

        List<Subtask> epicSubtasks = taskManager.getEpicSubtaskList(1);
        List<Subtask> subTasks = taskManager.getSubtaskList();
        history = taskManager.getHistory();

        assertNotNull(epicSubtasks, "Список подзадач эпика не возвращается");
        assertEquals(0, epicSubtasks.size(), "Неверное количество подзадач эпика");
        assertNotNull(subTasks, "Список подзадач не возвращается");
        assertEquals(0, subTasks.size(), "Неверное количество подзадач");
        assertNotNull(history, "История задач не возвращается");
        assertEquals(0, history.size(), "Неверный размер истории");
    }

    @Test
    public void shouldDeleteAllTasks() {
        Task task1 = new Task("a", "b", TaskStatus.NEW);
        Task task2 = new Task("a", "b", TaskStatus.NEW);

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);


        List<Task> history = taskManager.getHistory();

        assertNotNull(history, "История задач не возвращается");
        assertEquals(2, history.size(), "Неверный размер истории");

        taskManager.deleteAllTasks();

        List<Task> tasks = taskManager.getTaskList();
        history = taskManager.getHistory();

        assertNotNull(tasks, "Список задач не возвращается");
        assertEquals(0, tasks.size(), "Неверное количество задач");
        assertNotNull(history, "История задач не возвращается");
        assertEquals(0, history.size(), "Неверный размер истории");
    }

    @Test
    public void shouldDeleteAllEpics(){
        Epic epic1 = new Epic("a", "a");
        Epic epic2 = new Epic("b", "b");
        Subtask subTask = new Subtask("a", "b", TaskStatus.NEW, 1);

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subTask);
        taskManager.getEpicById(1);
        taskManager.getEpicById(2);
        taskManager.getSubtaskById(3);

        List<Task> history = taskManager.getHistory();

        assertNotNull(history, "История задач не возвращается");
        assertEquals(3, history.size(), "Неверный размер истории");

        taskManager.deleteAllEpics();

        List<Epic> epics = taskManager.getEpicList();
        List<Subtask> subTasks = taskManager.getSubtaskList();
        history = taskManager.getHistory();

        assertNotNull(epics, "Список эпиков не возвращается");
        assertEquals(0, epics.size(), "Неверное количество эпиков");
        assertNotNull(subTasks, "Список подзадач не возвращается");
        assertEquals(0, subTasks.size(), "Неверное количество подзадач");
        assertNotNull(history, "История задач не возвращается");
        assertEquals(0, history.size(), "Неверный размер истории");
    }

    @Test
    public void shouldDeleteAllSubtasks() {
        Epic epic = new Epic("a", "a");
        Subtask subTask1 = new Subtask("a", "b", TaskStatus.NEW, 1);
        Subtask subTask2 = new Subtask("b", "c", TaskStatus.NEW, 1);

        taskManager.addEpic(epic);
        taskManager.addSubtask(subTask1);
        taskManager.addSubtask(subTask2);
        taskManager.getEpicById(1);
        taskManager.getSubtaskById(2);
        taskManager.getSubtaskById(3);

        List<Task> history = taskManager.getHistory();

        assertNotNull(history, "История задач не возвращается");
        assertEquals(3, history.size(), "Неверный размер истории");

        taskManager.deleteAllSubtasks();

        List<Subtask> epicSubtasks = taskManager.getEpicSubtaskList(1);
        List<Subtask> subTasks = taskManager.getSubtaskList();
        history = taskManager.getHistory();

        assertNotNull(epicSubtasks, "Список подзадач эпика не возвращается");
        assertEquals(0, epicSubtasks.size(), "Неверное количество подзадач эпика");
        assertNotNull(subTasks, "Список подзадач не возвращается");
        assertEquals(0, subTasks.size(), "Неверное количество подзадач");
        assertNotNull(history, "История задач не возвращается");
        assertEquals(1, history.size(), "Неверный размер истории");
    }

    @Test
    public void shouldUpdateTask() {
        Task task = new Task("a", "b", TaskStatus.NEW);

        taskManager.addTask(task);

        String newName = "c";
        String newDescription = "d";
        TaskStatus newStatus = TaskStatus.IN_PROGRESS;
        Task newTask = new Task(newName, newDescription, 1, newStatus);

        taskManager.updateTask(newTask);
        task = taskManager.getTaskById(1);

        assertNotNull(task, "Задача не найдена");
        assertEquals(newName, task.getName(), "Неверное имя задачи");
        assertEquals(newDescription, task.getDescription(), "Неверное описание задачи");
        assertEquals(newStatus, task.getStatus(), "Неверный статус задачи");
    }

    @Test
    public void shouldUpdateEpic() {
        Epic epic = new Epic("a", "b");
        Subtask subTask = new Subtask("a", "b", TaskStatus.NEW, 1);

        taskManager.addEpic(epic);
        taskManager.addSubtask(subTask);
        epic = taskManager.getEpicById(1);

        String newName = "c";
        String newDescription = "d";
        Epic newEpic = new Epic(newName, newDescription, 1);

        taskManager.updateEpic(newEpic);
        newEpic = taskManager.getEpicById(1);

        assertNotNull(epic, "Задача не найдена");
        assertNotNull(newEpic, "Задача не найдена");
        assertEquals(newName, newEpic.getName(), "Неверное имя задачи");
        assertEquals(newDescription, newEpic.getDescription(), "Неверное описание задачи");
        assertEquals(epic.getStatus(), newEpic.getStatus(), "Неверный статус эпика");
        assertEquals(epic.getSubtaskIdList(), newEpic.getSubtaskIdList(), "Неверный список подзадач");
    }

    @Test
    public void shouldUpdateSubtask() {
        Epic epic = new Epic("a", "b");
        Subtask subTask = new Subtask("a", "b", TaskStatus.NEW, 1);

        taskManager.addEpic(epic);
        taskManager.addSubtask(subTask);

        String newName = "c";
        String newDescription = "d";
        TaskStatus newStatus = TaskStatus.IN_PROGRESS;
        Subtask newSubtask = new Subtask(newName, newDescription, 2, newStatus, 1);

        taskManager.updateSubtask(newSubtask);
        newSubtask = taskManager.getSubtaskById(2);

        assertNotNull(newSubtask, "Задача не найдена");
        assertEquals(newName, newSubtask.getName(), "Неверное имя задачи");
        assertEquals(newDescription, newSubtask.getDescription(), "Неверное описание задачи");
        assertEquals(newStatus, newSubtask.getStatus(), "Неверный статус задачи");
    }

    @Test
    public void testNotFoundException() {
        assertThrows(NotFoundException.class, () -> {
            taskManager.addTask(new Task("a", "a", TaskStatus.NEW));
            taskManager.getTaskById(2);
        }, "Отсутствие задачи должно приводить к исключению");
    }

    @Test
    public void testTimeIntersectionException() {
        assertThrows(TimeIntersectionException.class, () -> {
            taskManager.addTask(new Task("a", "a", TaskStatus.NEW,
                    LocalDateTime.of(2000, 1, 1, 0, 0), Duration.ofMinutes(20)));
            taskManager.addTask(new Task("a", "a", TaskStatus.NEW,
                    LocalDateTime.of(2000, 1, 1, 0, 10), Duration.ofMinutes(20)));
        }, "Пересечение времени должно приводить к исключению");
    }
}
