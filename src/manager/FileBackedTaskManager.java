package manager;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import exception.ManagerSaveException;
import task.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private static String toString(Task task) {
        TaskType taskClass = TaskType.TASK;
        if (task instanceof Epic) {
            taskClass = TaskType.EPIC;
        } else if (task instanceof Subtask) {
            taskClass = TaskType.SUBTASK;
        }

        String string = String.format("%d,%s,%s,%s,%s", task.getId(), taskClass, task.getName(),
                task.getStatus(), task.getDescription());
        if (task.getStartTime() != null) {
            string += "," + task.getStartTime().toString() + "," + task.getDuration().toMinutes();
        } else {
            string += "," + null + "," + 0;
        }
        if (taskClass == TaskType.SUBTASK) {
            string += "," + ((Subtask) task).getEpicId();
        }
        return string;
    }

    private void save() throws ManagerSaveException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write("id,type,name,status,description,start time,duration,epic\n");
            for (Task task : super.getTaskList()) {
                bw.write(toString(task) + "\n");
            }
            for (Epic epic : super.getEpicList()) {
                bw.write(toString(epic) + "\n");
            }
            for (Subtask subtask : super.getSubtaskList()) {
                bw.write(toString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл");
        }
    }

    private static Task fromString(String value) {
        String[] split = value.split(",");
        TaskStatus status = TaskStatus.valueOf(split[3]);
        LocalDateTime startTime = null;
        Duration duration = Duration.ZERO;

        if (!split[5].equals("null")) {
            startTime = LocalDateTime.parse(split[5], dtf);
            duration = Duration.ofMinutes(Integer.parseInt(split[6]));
        }
        switch (TaskType.valueOf(split[1])) {
            case TaskType.EPIC:
                Epic epic = new Epic(split[2], split[4], Integer.parseInt(split[0]));
                epic.setStatus(status);
                epic.setStartTime(startTime);
                epic.setDuration(duration);
                return epic;
            case TaskType.SUBTASK:
                return new Subtask(split[2], split[4], Integer.parseInt(split[0]), status, startTime, duration,
                        Integer.parseInt(split[7]));
            default:
                return new Task(split[2], split[4], Integer.parseInt(split[0]), status, startTime, duration);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        List<Task> taskList = new ArrayList<>();
        List<Epic> epicList = new ArrayList<>();
        List<Subtask> subtaskList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            while (br.ready()) {
                String line = br.readLine();
                if (line.isEmpty()) {
                    break;
                }
                Task task = fromString(line);

                if (task instanceof Epic) {
                    epicList.add((Epic) task);
                } else if (task instanceof Subtask) {
                    subtaskList.add((Subtask) task);
                } else {
                    taskList.add(task);
                }
                fileBackedTaskManager.setAllTasks(taskList, epicList, subtaskList);
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
            fm.addSubtask(new Subtask("Подзадача 1", "asd", TaskStatus.IN_PROGRESS,
                    fm.getCurrentId() - 1));
            fm.addTask(new Task("Задача 2", "345", TaskStatus.DONE));
            fm.addEpic(new Epic("Эпик 2", "666"));

            BufferedReader br = new BufferedReader(new FileReader(tempFile));
            while (br.ready()) {
                System.out.println(br.readLine());
            }
            br.close();

            FileBackedTaskManager fm2 = FileBackedTaskManager.loadFromFile(tempFile);
            printAllTasks(fm2);

            fm2.updateEpic(new Epic("Эпик 2", "777", 5));
            fm2.deleteEpicById(2);

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
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }
}
