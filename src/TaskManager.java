import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, SubTask> subTasks;
    private static int currentId = 1;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
    }

    public void addTask(Task task) {
        task.setId(currentId);
        tasks.put(currentId++, task);
    }

    public void addEpic(Epic epic) {
        epic.setId(currentId);
        if ((epic.getSubTaskIdList() == null) || epic.getSubTaskIdList().isEmpty()) {
            epics.put(currentId++, epic);
            return;
        }
        updateEpicSubTasks(epic);
        epics.put(currentId++, epic);
    }

    public void updateEpicSubTasks(Epic epic) {
        int newTasks = 0;
        int doneTasks = 0;
        SubTask subTask;

        for (Integer i : epic.getSubTaskIdList()) {
            subTask = subTasks.get(i);

            subTask.setEpicId(epic.getId());
            if (subTask.getStatus() == TaskStatus.NEW) {
                newTasks++;
            } else if (subTask.getStatus() == TaskStatus.DONE) {
                doneTasks++;
            }
        }
        epic.setNumberOfNewTasks(newTasks);
        epic.setNumberOfDoneTasks(doneTasks);
        epic.checkStatus();
    }

    public void addSubTask(SubTask subTask) {
        Epic epic = epics.get(subTask.getEpicId());

        subTask.setId(currentId);
        epic.getSubTaskIdList().add(currentId);
        subTasks.put(currentId++, subTask);
        if (subTask.getStatus() == TaskStatus.NEW) {
            epic.addNewTask();
        } else if (subTask.getStatus() == TaskStatus.DONE) {
            epic.addDoneTask();
        }
        epic.checkStatus();
    }

    public ArrayList<Task> getTaskList() {
        ArrayList<Task> taskList = new ArrayList<>();

        for (Integer i : tasks.keySet()) {
            taskList.add(tasks.get(i));
        }
        return  taskList;
    }

    public ArrayList<Epic> getEpicList() {
        ArrayList<Epic> epicList = new ArrayList<>();

        for (Integer i : epics.keySet()) {
            epicList.add(epics.get(i));
        }
        return  epicList;
    }

    public ArrayList<SubTask> getSubTaskList() {
        ArrayList<SubTask> subTaskList = new ArrayList<>();

        for (Integer i : subTasks.keySet()) {
            subTaskList.add(subTasks.get(i));
        }
        return subTaskList;
    }

    public ArrayList<SubTask> getEpicSubTaskList(int id) {
        ArrayList<SubTask> subTaskList = new ArrayList<>();
        ArrayList<Integer> subTaskIdList = epics.get(id).getSubTaskIdList();

        for (Integer i : subTaskIdList) {
            subTaskList.add(subTasks.get(i));
        }
        return  subTaskList;
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
    }

    public void deleteAllSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubTaskList();
        }
    }

    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        }
        return null;
    }

    public Epic getEpicById(int id) {
        if (epics.containsKey(id)) {
            return epics.get(id);
        }
        return null;
    }

    public SubTask getSubTaskById(int id) {
        if (subTasks.containsKey(id)) {
            return subTasks.get(id);
        }
        return null;
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        updateEpicSubTasks(epic);
        epics.put(epic.getId(), epic);
    }

    public void updateSubTask(SubTask newSubTask) {
        SubTask subTask = subTasks.get(newSubTask.getId());
        Epic epic = epics.get(subTask.getEpicId());

        if (subTask.getStatus() == TaskStatus.NEW) {
            epic.deleteNewTask();
        } else if (subTask.getStatus() == TaskStatus.DONE) {
            epic.deleteDoneTask();
        }
        if (newSubTask.getStatus() == TaskStatus.NEW) {
            epic.addNewTask();
        } else if (newSubTask.getStatus() == TaskStatus.DONE) {
            epic.addDoneTask();
        }
        epic.checkStatus();
        subTasks.put(newSubTask.getId(), newSubTask);
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteEpicById(int id) {
        epics.remove(id);
    }

    public void deleteSubTaskById(int id) {
        SubTask subTask = subTasks.get(id);
        Epic epic = epics.get(subTask.getEpicId());

        for (int i = 0; i < epic.getSubTaskIdList().size(); i++) {
            if (epic.getSubTaskIdList().get(i) == id) {
                epic.getSubTaskIdList().remove(i);
                break;
            }
        }
        if (subTask.getStatus() == TaskStatus.NEW) {
            epic.deleteNewTask();
        } else if (subTask.getStatus() == TaskStatus.DONE) {
            epic.deleteDoneTask();
        }
        epic.checkStatus();
        subTasks.remove(id);
    }
}
