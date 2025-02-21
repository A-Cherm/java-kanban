package manager;

import task.Epic;
import task.SubTask;
import task.Task;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TaskManager {
    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubTask(SubTask subTask);

    List<Task> getTaskList();

    List<Epic> getEpicList();

    List<SubTask> getSubTaskList();

    List<SubTask> getEpicSubTaskList(int id);

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubTasks();

    Optional<Task> getTaskById(int id);

    Optional<Epic> getEpicById(int id);

    Optional<SubTask> getSubTaskById(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubTaskById(int id);

    List<Task> getHistory();

    Map<Integer, Task> getTasks();

    Map<Integer, Epic> getEpics();

    Map<Integer, SubTask> getSubTasks();
}
