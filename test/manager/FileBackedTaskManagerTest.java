package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private File file;

    @BeforeEach
    public void newFileManager() {
        try {
            file = File.createTempFile("ManagerTest", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void shouldCreateStringFromTask() {
        Task task = new Task("Task1", "asd", 1, TaskStatus.DONE);
        String str = FileBackedTaskManager.toString(task);

        assertEquals("1,TASK,Task1,DONE,Description asd", str, "Неправильное преобразование в строку");

        SubTask subTask = new SubTask("Subtask1", "asd", 1, TaskStatus.NEW, 2);
        str = FileBackedTaskManager.toString(subTask);

        assertEquals("1,SUBTASK,Subtask1,NEW,Description asd,2", str, "Неправильное преобразование в строку");
    }

    @Test
    public void shouldCreateTaskFromString() {
        String str = "1,TASK,Task1,DONE,Description asd";
        Task task = FileBackedTaskManager.fromString(str);

        assertNotNull(task, "Задача не инициализирована");
        assertEquals("Task1", task.getName(), "Неправильное имя задачи");
        assertEquals("asd", task.getDescription(), "Неправильное описание задачи");
        assertEquals(TaskStatus.DONE, task.getStatus(), "Неправильный статус задачи");
    }

    @Test
    public void shouldLoadTasksFromFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))){
            bw.write("id,type,name,status,description,epic\n");
            bw.write("1,TASK,Task1,DONE,Description asd\n");
            bw.write("2,EPIC,Epic1,IN_PROGRESS,Description 111\n");
            bw.write("3,SUBTASK,Subtask1,DONE,Description 112,2\n");
            bw.write("4,SUBTASK,Subtask2,NEW,Description 113,2\n");
            bw.write("5,EPIC,Epic2,DONE,Description 222\n");
            bw.close();

            FileBackedTaskManager fileManager = FileBackedTaskManager.loadFromFile(file);

            assertEquals(1, fileManager.getTasks().size(), "Неправильное число задач");
            assertEquals(2, fileManager.getEpics().size(), "Неправильное число эпиков");
            assertEquals(2, fileManager.getSubTasks().size(), "Неправильное число подзадач");
            assertEquals(2, fileManager.getEpicList().getFirst().getSubTaskIdList().size(),
                    "Неправильное число подзадач эпика");
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла");
        }
    }

    @Test
    public void shouldSaveTasksToFile() {
        FileBackedTaskManager fileManager = FileBackedTaskManager.loadFromFile(file);
        int id = InMemoryTaskManager.getCurrentId();

        fileManager.addTask(new Task("Task1", "aaa", TaskStatus.NEW));
        int epicId = InMemoryTaskManager.getCurrentId();
        fileManager.addEpic(new Epic("Epic1", "111"));
        fileManager.addSubTask(new SubTask("Subtask1", "112", TaskStatus.NEW, epicId));
        fileManager.addSubTask(new SubTask("Subtask2", "113", TaskStatus.IN_PROGRESS, epicId));
        fileManager.addEpic(new Epic("Epic2", "222"));
        fileManager.addTask(new Task("Task2", "bbb",TaskStatus.DONE));

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            assertEquals(id + ",TASK,Task1,NEW,Description aaa", br.readLine(),
                    "Неправильная запись задачи в файл");
            assertEquals((id + 5) + ",TASK,Task2,DONE,Description bbb", br.readLine(),
                    "Неправильная запись задачи в файл");
            assertEquals((id + 1) + ",EPIC,Epic1,IN_PROGRESS,Description 111", br.readLine(),
                    "Неправильная запись эпика в файл");
            assertEquals((id + 4) + ",EPIC,Epic2,NEW,Description 222", br.readLine(),
                    "Неправильная запись эпика в файл");
            assertEquals((id + 2) + ",SUBTASK,Subtask1,NEW,Description 112," + (id + 1), br.readLine(),
                    "Неправильная запись подзадачи в файл");
            assertEquals((id + 3) + ",SUBTASK,Subtask2,IN_PROGRESS,Description 113," + (id + 1), br.readLine(),
                    "Неправильная запись подзадачи в файл");
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла");
        }
    }

}