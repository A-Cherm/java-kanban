import manager.InMemoryTaskManager;
import manager.TaskManager;
import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;

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
        SubTask newSubTask = new SubTask("Подзадача 1", "", TaskStatus.IN_PROGRESS, 3);
        taskManager.addSubTask(newSubTask);
        newSubTask = new SubTask("Подзадача 2", "", TaskStatus.NEW, 3);
        taskManager.addSubTask(newSubTask);
        newEpic = new Epic("Эпик 2", "");
        taskManager.addEpic(newEpic);
        newSubTask = new SubTask("Подзадача 3", "", TaskStatus.DONE, 6);
        taskManager.addSubTask(newSubTask);

        System.out.println("Добавление");
        printAllTasks(taskManager);


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
        printAllTasks(taskManager);

        // удаление
        taskManager.deleteTaskById(1);
        taskManager.deleteTaskById(10);
        taskManager.deleteSubTaskById(4);
        taskManager.deleteSubTaskById(7);

        System.out.println("Удаление");
        printAllTasks(taskManager);

        taskManager.deleteAllEpics();

        printAllTasks(taskManager);

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
        printAllTasks(taskManager);

        taskManager.deleteAllSubTasks();

        printAllTasks(taskManager);

        newSubTask = new SubTask("Подзадача 2", "", TaskStatus.DONE, 8);
        taskManager.addSubTask(newSubTask);
        newSubTask = new SubTask("Подзадача 3", "", TaskStatus.DONE, 8);
        taskManager.addSubTask(newSubTask);

        printAllTasks(taskManager);

        // история
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();

        newTask = new Task("Задача 1", "123", TaskStatus.NEW);
        taskManager.addTask(newTask);
        newTask = new Task("Задача 2", "321", TaskStatus.DONE);
        taskManager.addTask(newTask);

        int id = InMemoryTaskManager.getCurrentId();
        newEpic = new Epic("Эпик 1", "");
        taskManager.addEpic(newEpic);
        newSubTask = new SubTask("Подзадача 1", "", TaskStatus.IN_PROGRESS, id);
        taskManager.addSubTask(newSubTask);
        newSubTask = new SubTask("Подзадача 2", "", TaskStatus.NEW, id);
        taskManager.addSubTask(newSubTask);
        newSubTask = new SubTask("Подзадача 3", "", TaskStatus.NEW, id);
        taskManager.addSubTask(newSubTask);
        newEpic = new Epic("Эпик 2", "");
        taskManager.addEpic(newEpic);
        printAllTasks(taskManager);

        taskManager.getTaskById(id - 2);
        taskManager.getTaskById(id - 2);

        printHistory(taskManager);

        taskManager.getTaskById(id - 1);
        taskManager.deleteTaskById(id - 1);

        printHistory(taskManager);

        taskManager.getSubTaskById(id + 1);
        taskManager.getTaskById(id - 1);
        taskManager.getSubTaskById(id + 3);
        taskManager.getSubTaskById(id + 2);

        printHistory(taskManager);

        taskManager.getTaskById(id - 2);
        taskManager.getSubTaskById(id + 1);
        taskManager.getEpicById(id + 4);
        taskManager.getEpicById(id + 4);

        printHistory(taskManager);

        taskManager.deleteTaskById(id - 2);

        printHistory(taskManager);

        taskManager.deleteEpicById(id);

        printHistory(taskManager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTaskList()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpicList()) {
            System.out.println(epic);

            for (Task task : manager.getEpicSubTaskList(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubTaskList()) {
            System.out.println(subtask);
        }
        System.out.println("-".repeat(20));
    }

    private static void printHistory(TaskManager manager) {
        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
        System.out.println("-".repeat(20));
    }
}
