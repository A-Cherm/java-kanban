package manager;

import exception.ManagerSaveException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File file;

    @BeforeEach
    public void newFileManager() {
        try {
            file = File.createTempFile("ManagerTest", ".csv");
            taskManager = new FileBackedTaskManager(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    public static void testManagerSaveException() {
        assertThrows(ManagerSaveException.class, () -> {
            TaskManager testManager = new FileBackedTaskManager(new File("Test"));
            testManager.addTask(new Task("a", "a", TaskStatus.NEW));
        }, "Ошибка сохранения должна приводить к исключению");
    }

    @Test
    public void shouldLoadTasksFromFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))){
            bw.write("id,type,name,status,description,start time,duration,epic\n");
            bw.write("1,TASK,Task1,DONE,asd,null,0\n");
            bw.write("2,EPIC,Epic1,IN_PROGRESS,111,null,0\n");
            bw.write("3,SUBTASK,Subtask1,DONE,112,null,0,2\n");
            bw.write("4,SUBTASK,Subtask2,NEW,113,null,0,2\n");
            bw.write("5,EPIC,Epic2,DONE,222,null,0,\n");
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

        fileManager.addTask(new Task("Task1", "aaa", TaskStatus.NEW));
        fileManager.addEpic(new Epic("Epic1", "111"));
        fileManager.addSubTask(new SubTask("Subtask1", "112", TaskStatus.NEW, 2));
        fileManager.addSubTask(new SubTask("Subtask2", "113", TaskStatus.IN_PROGRESS, 2));
        fileManager.addEpic(new Epic("Epic2", "222"));
        fileManager.addTask(new Task("Task2", "bbb",TaskStatus.DONE));

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            assertEquals(1 + ",TASK,Task1,NEW,aaa,null,0", br.readLine(),
                    "Неправильная запись задачи в файл");
            assertEquals(6 + ",TASK,Task2,DONE,bbb,null,0", br.readLine(),
                    "Неправильная запись задачи в файл");
            assertEquals(2 + ",EPIC,Epic1,IN_PROGRESS,111,null,0", br.readLine(),
                    "Неправильная запись эпика в файл");
            assertEquals(5 + ",EPIC,Epic2,NEW,222,null,0", br.readLine(),
                    "Неправильная запись эпика в файл");
            assertEquals(3 + ",SUBTASK,Subtask1,NEW,112,null,0," + 2, br.readLine(),
                    "Неправильная запись подзадачи в файл");
            assertEquals(4 + ",SUBTASK,Subtask2,IN_PROGRESS,113,null,0," + 2, br.readLine(),
                    "Неправильная запись подзадачи в файл");
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла");
        }
    }

}