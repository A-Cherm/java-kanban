package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    public void newTaskManager() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void shouldAddTaskAndReturn() {
        Task task1 = new Task("a", "b", TaskStatus.NEW);
        int id = InMemoryTaskManager.getCurrentId();
        taskManager.addTask(task1);

        Task task2 = taskManager.getTaskById(id);

        assertNotNull(task2, "Задача не найдена");
        assertEquals(task1, task2, "Задачи не совпадают");
        assertEquals(task1.getName(), task2.getName(), "Имя задач не совпадает");
        assertEquals(task1.getDescription(), task2.getDescription(), "Описание задач не совпадает");
        assertEquals(task1.getStatus(), task2.getStatus(), "Статус задач не совпадает");

        List<Task> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Список задач не возвращается");
        assertEquals(1, tasks.size(), "Неверное количество задач");
        assertEquals(task1, tasks.getFirst(), "Задачи не совпадают");
    }

    @Test
    public void shouldAddEpicAndReturn() {
        Epic epic1 = new Epic("a", "a");
        int id = InMemoryTaskManager.getCurrentId();

        taskManager.addEpic(epic1);

        Epic epic2 = taskManager.getEpicById(id);

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
    public void shouldAddSubTaskAndReturn() {
        Epic epic = new Epic("a", "a");
        int id = InMemoryTaskManager.getCurrentId();
        SubTask subTask1 = new SubTask("a", "b", TaskStatus.NEW, id);

        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask1);

        SubTask subTask2 = taskManager.getSubTaskById(id + 1);

        assertNotNull(subTask2, "Подзадача не найдена");
        assertEquals(subTask1, subTask2, "Подзадачи не совпадают");
        assertEquals(subTask1.getName(), subTask2.getName(), "Имя подзадач не совпадает");
        assertEquals(subTask1.getDescription(), subTask2.getDescription(), "Описание подзадач не совпадает");
        assertEquals(subTask1.getStatus(), subTask2.getStatus(), "Статус подзадач не совпадает");

        List<SubTask> subTasks = taskManager.getSubTaskList();

        assertNotNull(subTasks, "Список подзадач не возвращается");
        assertEquals(1, subTasks.size(), "Неверное количество подзадач");
        assertEquals(subTask1, subTasks.getFirst(), "Подзадачи не совпадают");
    }

    // проверка добвления задачи с уже заданным id
    @Test
    public void shouldAddTaskWithCorrectId() {
        Task task1 = new Task("a", "b", 5, TaskStatus.NEW);
        int id = InMemoryTaskManager.getCurrentId();
        taskManager.addTask(task1);

        Task task2 = taskManager.getTaskById(id);

        assertNotNull(task2, "Задача не найдена");
        assertEquals(task1, task2, "Задачи не совпадают");
        assertEquals(id, task2.getId(), "Неверный индекс задачи");
    }

    @Test
    public void shouldUpdateEpicStatus() {
        Epic epic = new Epic("a", "a");
        int id = InMemoryTaskManager.getCurrentId();
        SubTask subTask1 = new SubTask("a", "b", TaskStatus.IN_PROGRESS, id);

        taskManager.addEpic(epic);
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Неверный статус пустого эпика");

        taskManager.addSubTask(subTask1);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Неверный статус эпика");

        SubTask subTask2 = new SubTask("a", "b", id + 1, TaskStatus.DONE, id);

        taskManager.updateSubTask(subTask2);
        assertEquals(TaskStatus.DONE, epic.getStatus(), "Неверный статус эпика");
    }

    @Test
    public void shouldReturnEpicSubTaskList() {
        Epic epic = new Epic("a", "a");
        int id = InMemoryTaskManager.getCurrentId();
        SubTask subTask = new SubTask("a", "b", TaskStatus.NEW, id);

        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask);

        List<SubTask> epicSubTaskList = taskManager.getEpicSubTaskList(id);

        assertNotNull(epicSubTaskList, "Список подзадач эпика не возвращается");
        assertEquals(1, epicSubTaskList.size(), "Неверное количество подзадач");
        assertEquals(subTask, epicSubTaskList.getFirst(), "Подзадачи не совпадают");
    }

    @Test
    public void shouldDeleteTaskById() {
        Task task = new Task("a", "b", TaskStatus.NEW);
        int id = InMemoryTaskManager.getCurrentId();
        taskManager.addTask(task);
        taskManager.getTaskById(id);

        List<Task> history = taskManager.getHistory();

        assertNotNull(history, "История задач не возвращается");
        assertEquals(1, history.size(), "Неверный размер истории");

        taskManager.deleteTaskById(id);

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
        int id = InMemoryTaskManager.getCurrentId();
        SubTask subTask = new SubTask("a", "b", TaskStatus.NEW, id);

        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask);
        taskManager.getEpicById(id);
        taskManager.getSubTaskById(id + 1);

        List<Task> history = taskManager.getHistory();

        assertNotNull(history, "История задач не возвращается");
        assertEquals(2, history.size(), "Неверный размер истории");

        taskManager.deleteEpicById(id);

        List<Epic> epics = taskManager.getEpicList();
        List<SubTask> subTasks = taskManager.getSubTaskList();
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
        int id = InMemoryTaskManager.getCurrentId();
        SubTask subTask = new SubTask("a", "b", TaskStatus.NEW, id);

        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask);
        taskManager.getSubTaskById(id + 1);

        List<Task> history = taskManager.getHistory();

        assertNotNull(history, "История задач не возвращается");
        assertEquals(1, history.size(), "Неверный размер истории");

        taskManager.deleteSubTaskById(id + 1);

        List<SubTask> epicSubTasks = taskManager.getEpicSubTaskList(id);
        List<SubTask> subTasks = taskManager.getSubTaskList();
        history = taskManager.getHistory();

        assertNotNull(epicSubTasks, "Список подзадач эпика не возвращается");
        assertEquals(0, epicSubTasks.size(), "Неверное количество подзадач эпика");
        assertNotNull(subTasks, "Список подзадач не возвращается");
        assertEquals(0, subTasks.size(), "Неверное количество подзадач");
        assertNotNull(history, "История задач не возвращается");
        assertEquals(0, history.size(), "Неверный размер истории");
    }

    @Test
    public void shouldDeleteAllTasks() {
        Task task1 = new Task("a", "b", TaskStatus.NEW);
        Task task2 = new Task("a", "b", TaskStatus.NEW);

        int id = InMemoryTaskManager.getCurrentId();
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.getTaskById(id);
        taskManager.getTaskById(id + 1);


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
        int id = InMemoryTaskManager.getCurrentId();
        SubTask subTask = new SubTask("a", "b", TaskStatus.NEW, id);

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubTask(subTask);
        taskManager.getEpicById(id);
        taskManager.getEpicById(id + 1);
        taskManager.getSubTaskById(id + 2);

        List<Task> history = taskManager.getHistory();

        assertNotNull(history, "История задач не возвращается");
        assertEquals(3, history.size(), "Неверный размер истории");

        taskManager.deleteAllEpics();

        List<Epic> epics = taskManager.getEpicList();
        List<SubTask> subTasks = taskManager.getSubTaskList();
        history = taskManager.getHistory();

        assertNotNull(epics, "Список эпиков не возвращается");
        assertEquals(0, epics.size(), "Неверное количество эпиков");
        assertNotNull(subTasks, "Список подзадач не возвращается");
        assertEquals(0, subTasks.size(), "Неверное количество подзадач");
        assertNotNull(history, "История задач не возвращается");
        assertEquals(0, history.size(), "Неверный размер истории");
    }

    @Test
    public void shouldDeleteAllSubTasks() {
        Epic epic = new Epic("a", "a");
        int id = InMemoryTaskManager.getCurrentId();
        SubTask subTask1 = new SubTask("a", "b", TaskStatus.NEW, id);
        SubTask subTask2 = new SubTask("b", "c", TaskStatus.NEW, id);

        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.getEpicById(id);
        taskManager.getSubTaskById(id + 1);
        taskManager.getSubTaskById(id + 2);

        List<Task> history = taskManager.getHistory();

        assertNotNull(history, "История задач не возвращается");
        assertEquals(3, history.size(), "Неверный размер истории");

        taskManager.deleteAllSubTasks();

        List<SubTask> epicSubTasks = taskManager.getEpicSubTaskList(id);
        List<SubTask> subTasks = taskManager.getSubTaskList();
        history = taskManager.getHistory();

        assertNotNull(epicSubTasks, "Список подзадач эпика не возвращается");
        assertEquals(0, epicSubTasks.size(), "Неверное количество подзадач эпика");
        assertNotNull(subTasks, "Список подзадач не возвращается");
        assertEquals(0, subTasks.size(), "Неверное количество подзадач");
        assertNotNull(history, "История задач не возвращается");
        assertEquals(1, history.size(), "Неверный размер истории");
    }

    @Test
    public void shouldUpdateTask() {
        Task task = new Task("a", "b", TaskStatus.NEW);
        int id = InMemoryTaskManager.getCurrentId();

        taskManager.addTask(task);

        String newName = "c";
        String newDescription = "d";
        TaskStatus newStatus = TaskStatus.IN_PROGRESS;
        Task newTask = new Task(newName, newDescription, id, newStatus);

        taskManager.updateTask(newTask);
        task = taskManager.getTaskById(id);

        assertNotNull(task, "Задача не найдена");
        assertEquals(newName, task.getName(), "Неверное имя задачи");
        assertEquals(newDescription, task.getDescription(), "Неверное описание задачи");
        assertEquals(newStatus, task.getStatus(), "Неверный статус задачи");
    }

    @Test
    public void shouldUpdateEpic() {
        Epic epic = new Epic("a", "b");
        int id = InMemoryTaskManager.getCurrentId();
        SubTask subTask = new SubTask("a", "b", TaskStatus.NEW, id);

        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask);
        epic = taskManager.getEpicById(id);

        String newName = "c";
        String newDescription = "d";
        Epic newEpic = new Epic(newName, newDescription, id);

        taskManager.updateEpic(newEpic);
        newEpic = taskManager.getEpicById(id);

        assertNotNull(newEpic, "Задача не найдена");
        assertEquals(newName, newEpic.getName(), "Неверное имя задачи");
        assertEquals(newDescription, newEpic.getDescription(), "Неверное описание задачи");
        assertEquals(epic.getStatus(), newEpic.getStatus(), "Неверный статус эпика");
        assertEquals(epic.getSubTaskIdList(), newEpic.getSubTaskIdList(), "Неверный список подзадач");
    }

    @Test
    public void shouldUpdateSubTask() {
        Epic epic = new Epic("a", "b");
        int id = InMemoryTaskManager.getCurrentId();
        SubTask subTask = new SubTask("a", "b", TaskStatus.NEW, id);

        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask);

        String newName = "c";
        String newDescription = "d";
        TaskStatus newStatus = TaskStatus.IN_PROGRESS;
        SubTask newSubTask = new SubTask(newName, newDescription, id + 1, newStatus, id);

        taskManager.updateSubTask(newSubTask);
        newSubTask = taskManager.getSubTaskById(id + 1);

        assertNotNull(newSubTask, "Задача не найдена");
        assertEquals(newName, newSubTask.getName(), "Неверное имя задачи");
        assertEquals(newDescription, newSubTask.getDescription(), "Неверное описание задачи");
        assertEquals(newStatus, newSubTask.getStatus(), "Неверный статус задачи");
    }
}