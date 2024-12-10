import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        // тесты работы методов
        // добавление задач, эпиков, подзадач
        Task newTask = new Task("Задача 1", "123", TaskStatus.NEW);
        taskManager.addTask(newTask);
        newTask = new Task("Задача 2", "321", TaskStatus.DONE);
        taskManager.addTask(newTask);
        Epic newEpic = new Epic("Эпик 1", "");
        taskManager.addEpic(newEpic);
        SubTask newSubTask = new SubTask("Подзадача 1", "", TaskStatus.IN_PROGRESS, 3);
        taskManager.addSubTask(newSubTask);
        newSubTask = new SubTask("Подзадача 2", "", TaskStatus.NEW, 3);
        taskManager.addSubTask(newSubTask);
        newEpic = new Epic("Эпик 2", "");
        taskManager.addEpic(newEpic);
        newSubTask = new SubTask("Подзадача 3", "", TaskStatus.DONE, 6);
        taskManager.addSubTask(newSubTask);

        System.out.println("Добавление");
        System.out.println("Tasks: " + taskManager.getTaskList());
        System.out.println("Epics: " + taskManager.getEpicList());
        System.out.println("SubTasks " + taskManager.getSubTaskList());
        System.out.println(taskManager.getEpicSubTaskList(3));
        System.out.println(taskManager.getEpicSubTaskList(6));
        System.out.println("-".repeat(20));

        // обновление задач
        newTask = new Task("Задача 1", "111", 1, TaskStatus.IN_PROGRESS);
        taskManager.updateTask(newTask);
        newSubTask = new SubTask("Подзадача 1", "", 4, TaskStatus.DONE, 3);
        taskManager.updateSubTask(newSubTask);
        newSubTask = new SubTask("Подзадача 2", "", 5, TaskStatus.DONE, 3);
        taskManager.updateSubTask(newSubTask);
        newEpic = new Epic("Эпик 2", "новый!",3);
        taskManager.updateEpic(newEpic);

        System.out.println("Обновление");
        System.out.println("Tasks: " + taskManager.getTaskList());
        System.out.println("Epics: " + taskManager.getEpicList());
        System.out.println("SubTasks: " + taskManager.getSubTaskList());
        System.out.println(taskManager.getEpicSubTaskList(3));
        System.out.println("-".repeat(20));

        // удаление
        taskManager.deleteTaskById(1);
        taskManager.deleteTaskById(10);
        taskManager.deleteSubTaskById(4);
        taskManager.deleteSubTaskById(7);

        System.out.println("Удаление");
        System.out.println("Tasks: " + taskManager.getTaskList());
        System.out.println("Epics: " + taskManager.getEpicList());
        System.out.println("SubTasks: " + taskManager.getSubTaskList());
        System.out.println(taskManager.getEpicSubTaskList(3));
        System.out.println(taskManager.getEpicSubTaskList(6));
        System.out.println("-".repeat(20));

        taskManager.deleteAllEpics();

        System.out.println("Tasks: " + taskManager.getTaskList());
        System.out.println("Epics: " + taskManager.getEpicList());
        System.out.println("SubTasks: " + taskManager.getSubTaskList());
        System.out.println("-".repeat(20));

        // удаление всех подзадач
        taskManager.deleteEpicById(8);
        taskManager.deleteAllTasks();
        newEpic = new Epic("Эпик 1", "");
        taskManager.addEpic(newEpic);
        newSubTask = new SubTask("Подзадача 1", "", TaskStatus.IN_PROGRESS, 9);
        taskManager.addSubTask(newSubTask);
        newSubTask = new SubTask("Подзадача 2", "", TaskStatus.NEW, 8);
        taskManager.addSubTask(newSubTask);
        newSubTask = new SubTask("Подзадача 3", "", TaskStatus.NEW, 8);
        taskManager.addSubTask(newSubTask);

        System.out.println("Удаление всех подзадач");
        System.out.println("Tasks: " + taskManager.getTaskList());
        System.out.println("Epics: " + taskManager.getEpicList());
        System.out.println("SubTasks: " + taskManager.getSubTaskList());
        System.out.println(taskManager.getEpicSubTaskList(9));
        System.out.println("-".repeat(20));

        taskManager.deleteAllSubTasks();

        System.out.println("Tasks: " + taskManager.getTaskList());
        System.out.println("Epics: " + taskManager.getEpicList());
        System.out.println("SubTasks: " + taskManager.getSubTaskList());
        System.out.println(taskManager.getEpicSubTaskList(9));
        System.out.println("-".repeat(20));

        newSubTask = new SubTask("Подзадача 2", "", TaskStatus.DONE, 8);
        taskManager.addSubTask(newSubTask);
        newSubTask = new SubTask("Подзадача 3", "", TaskStatus.DONE, 8);
        taskManager.addSubTask(newSubTask);

        System.out.println("Tasks: " + taskManager.getTaskList());
        System.out.println("Epics: " + taskManager.getEpicList());
        System.out.println("SubTasks: " + taskManager.getSubTaskList());
        System.out.println(taskManager.getEpicSubTaskList(9));
        System.out.println("-".repeat(20));
    }
}
