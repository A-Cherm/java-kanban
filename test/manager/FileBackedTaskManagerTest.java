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
    public void shouldLoadTasksFromFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))){
            bw.write("id,type,name,status,description,epic\n");
            bw.write("1,TASK,Task1,DONE,asd\n");
            bw.write("2,EPIC,Epic1,IN_PROGRESS,111\n");
            bw.write("3,SUBTASK,Subtask1,DONE,112,2\n");
            bw.write("4,SUBTASK,Subtask2,NEW,113,2\n");
            bw.write("5,EPIC,Epic2,DONE,222\n");
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
            assertEquals(1 + ",TASK,Task1,NEW,aaa", br.readLine(),
                    "Неправильная запись задачи в файл");
            assertEquals(6 + ",TASK,Task2,DONE,bbb", br.readLine(),
                    "Неправильная запись задачи в файл");
            assertEquals(2 + ",EPIC,Epic1,IN_PROGRESS,111", br.readLine(),
                    "Неправильная запись эпика в файл");
            assertEquals(5 + ",EPIC,Epic2,NEW,222", br.readLine(),
                    "Неправильная запись эпика в файл");
            assertEquals(3 + ",SUBTASK,Subtask1,NEW,112," + 2, br.readLine(),
                    "Неправильная запись подзадачи в файл");
            assertEquals(4 + ",SUBTASK,Subtask2,IN_PROGRESS,113," + 2, br.readLine(),
                    "Неправильная запись подзадачи в файл");
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла");
        }
    }

}