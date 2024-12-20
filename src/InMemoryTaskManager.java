import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, SubTask> subTasks;
    private HistoryManager historyManager;
    private static int currentId = 1;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public void addTask(Task task) {
        task.setId(currentId);
        tasks.put(currentId++, task);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(currentId);
        updateEpicStatus(epic);
        epics.put(currentId++, epic);
    }

    private void updateEpicStatus(Epic epic) {
        boolean isAllNewTasks = true;
        boolean isAllDoneTasks = true;
        SubTask subTask;

        for (Integer i : epic.getSubTaskIdList()) {
            subTask = subTasks.get(i);

            if (subTask.getStatus() == TaskStatus.NEW) {
                isAllDoneTasks = false;
            } else if (subTask.getStatus() == TaskStatus.DONE) {
                isAllNewTasks = false;
            } else {
                isAllDoneTasks = false;
                isAllNewTasks = false;
                break;
            }
        }
        if (isAllNewTasks) {
            epic.setStatus(TaskStatus.NEW);
        } else if (isAllDoneTasks) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public void addSubTask(SubTask subTask) {
        if (!epics.containsKey(subTask.getEpicId())) {
            return;
        }
        Epic epic = epics.get(subTask.getEpicId());

        subTask.setId(currentId);
        epic.addSubTaskId(currentId);
        subTasks.put(currentId++, subTask);
        updateEpicStatus(epic);
    }

    @Override
    public ArrayList<Task> getTaskList() {
        ArrayList<Task> taskList = new ArrayList<>();

        for (Integer i : tasks.keySet()) {
            taskList.add(tasks.get(i));
        }
        return  taskList;
    }

    @Override
    public ArrayList<Epic> getEpicList() {
        ArrayList<Epic> epicList = new ArrayList<>();

        for (Integer i : epics.keySet()) {
            epicList.add(epics.get(i));
        }
        return  epicList;
    }

    @Override
    public ArrayList<SubTask> getSubTaskList() {
        ArrayList<SubTask> subTaskList = new ArrayList<>();

        for (Integer i : subTasks.keySet()) {
            subTaskList.add(subTasks.get(i));
        }
        return subTaskList;
    }

    @Override
    public ArrayList<SubTask> getEpicSubTaskList(int id) {
        if (!epics.containsKey(id)) {
            return null;
        }
        ArrayList<SubTask> subTaskList = new ArrayList<>();
        ArrayList<Integer> subTaskIdList = epics.get(id).getSubTaskIdList();

        for (Integer i : subTaskIdList) {
            subTaskList.add(subTasks.get(i));
        }
        return  subTaskList;
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubTaskList();
        }
    }

    @Override
    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            historyManager.addTask(tasks.get(id));
            return tasks.get(id);
        }
        return null;
    }

    @Override
    public Epic getEpicById(int id) {
        if (epics.containsKey(id)) {
            historyManager.addTask(epics.get(id));
            return epics.get(id);
        }
        return null;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        if (subTasks.containsKey(id)) {
            historyManager.addTask(subTasks.get(id));
            return subTasks.get(id);
        }
        return null;
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic currentEpic = epics.get(epic.getId());

            currentEpic.setName(epic.getName());
            currentEpic.setDescription(epic.getDescription());
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTasks.containsKey(subTask.getId())
            && subTasks.get(subTask.getId()).getEpicId() == subTask.getEpicId()) {
            subTasks.put(subTask.getId(), subTask);
            updateEpicStatus(epics.get(subTask.getEpicId()));
        }
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            for (Integer i : epics.get(id).getSubTaskIdList()) {
                deleteSubTaskById(i);
            }
            epics.remove(id);
        }
    }

    @Override
    public void deleteSubTaskById(int id) {
        if (subTasks.containsKey(id)) {
            Epic epic = epics.get(subTasks.get(id).getEpicId());

            epic.deleteSubTaskId(id);
            updateEpicStatus(epic);
            subTasks.remove(id);
        }
    }

    public static int getCurrentId() {
        return currentId;
    }

    @Override
    public HashMap<Integer, Task> getTasks() {
        return tasks == null ? null : new HashMap<>(tasks);
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        return epics == null ? null : new HashMap<>(epics);
    }

    @Override
    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks == null ? null : new HashMap<>(subTasks);
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }
}