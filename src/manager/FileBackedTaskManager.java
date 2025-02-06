package manager;

import java.io.*;
import java.util.HashMap;

import task.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static String toString(Task task) {
        TaskType taskClass = TaskType.TASK;
        if (task instanceof Epic) {
            taskClass = TaskType.EPIC;
        } else if (task instanceof SubTask) {
            taskClass = TaskType.SUBTASK;
        }

        String string = String.format("%d,%s,%s,%s,Description %s", task.getId(), taskClass, task.getName(),
                task.getStatus(), task.getDescription());


        if (task instanceof SubTask subTask) {
            string += "," + subTask.getEpicId();
        }
        return string;
    }

    public void save() throws ManagerSaveException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write("id,type,name,status,description,epic\n");
            for (Task task : super.getTaskList()) {
                bw.write(toString(task) + "\n");
            }
            for (Epic epic : super.getEpicList()) {
                bw.write(toString(epic) + "\n");
            }
            for (SubTask subTask : super.getSubTaskList()) {
                bw.write(toString(subTask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл");
        }
    }

    public static Task fromString(String value) {
        String[] split = value.split(",");
        TaskStatus status = switch (split[3]) {
            case "IN_PROGRESS" -> TaskStatus.IN_PROGRESS;
            case "DONE" -> TaskStatus.DONE;
            default -> TaskStatus.NEW;
        };

        return switch (split[1]) {
            case "EPIC" -> new Epic(split[2], split[4].substring(12), Integer.parseInt(split[0]));
            case "SUBTASK" -> new SubTask(split[2], split[4].substring(12), Integer.parseInt(split[0]),
                                  status, Integer.parseInt(split[5]));
            default -> new Task(split[2], split[4].substring(12), Integer.parseInt(split[0]), status);
        };
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        HashMap<Integer, Integer> epicIdChange = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            while (br.ready()) {
                String line = br.readLine();
                if (line.isEmpty()) {
                    break;
                }
                Task task = fromString(line);
                if (task instanceof Epic) {
                    epicIdChange.put(task.getId(), FileBackedTaskManager.getCurrentId());
                    fileBackedTaskManager.addEpic((Epic) task);
                } else if (task instanceof SubTask) {
                    ((SubTask) task).setEpicId(epicIdChange.get(((SubTask) task).getEpicId()));
                    fileBackedTaskManager.addSubTask((SubTask) task);
                } else {
                    fileBackedTaskManager.addTask(task);
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка загрузки из файла");
        }
        return fileBackedTaskManager;
    }

    public static void main(String[] args) {
        try {
            File tempFile = File.createTempFile("managerTest", ".csv");
            FileBackedTaskManager fm = new FileBackedTaskManager(tempFile);
            fm.addTask(new Task("Задача 1", "123", TaskStatus.NEW));
            fm.addEpic(new Epic("Эпик 1", "321"));
            fm.addSubTask(new SubTask("Подзадача 1", "asd", TaskStatus.IN_PROGRESS,
                    InMemoryTaskManager.getCurrentId() - 1));
            fm.addTask(new Task("Задача 2", "345", TaskStatus.DONE));
            fm.addEpic(new Epic("Эпик 2", "666"));

            BufferedReader br = new BufferedReader(new FileReader(tempFile));
            while (br.ready()) {
                System.out.println(br.readLine());
            }
            br.close();

            FileBackedTaskManager fm2 = FileBackedTaskManager.loadFromFile(tempFile);
            printAllTasks(fm2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        save();
    }

}
