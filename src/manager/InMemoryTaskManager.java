package manager;

import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, SubTask> subTasks;
    private final Set<Task> prioritizedTasks;
    private final HistoryManager historyManager;
    private int currentId = 1;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
        prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    @Override
    public void addTask(Task task) {
        if (task.getStartTime() != null) {
            if (checkTimeIntersections(task)) {
                return;
            }
            prioritizedTasks.add(task);
        }
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
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        Duration epicDuration = Duration.ofMinutes(0);
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
            }
            if (subTask.getStartTime() != null) {
                if (startTime == null) {
                    startTime = subTask.getStartTime();
                    endTime = subTask.getEndTime();
                } else {
                    if (startTime.isAfter(subTask.getStartTime())) {
                        startTime = subTask.getStartTime();
                    }
                    if (endTime.isBefore(subTask.getEndTime())) {
                        endTime = subTask.getEndTime();
                    }
                }
                epicDuration = epicDuration.plus(subTask.getDuration());
            }
        }
        if (isAllNewTasks) {
            epic.setStatus(TaskStatus.NEW);
        } else if (isAllDoneTasks) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
        epic.setDuration(epicDuration);
    }

    @Override
    public void addSubTask(SubTask subTask) {
        if (!epics.containsKey(subTask.getEpicId())) {
            return;
        }
        if (subTask.getStartTime() != null) {
            if (checkTimeIntersections(subTask)) {
                return;
            }
            prioritizedTasks.add(subTask);
        }
        Epic epic = epics.get(subTask.getEpicId());

        subTask.setId(currentId);
        epic.addSubTaskId(currentId);
        subTasks.put(currentId++, subTask);
        updateEpicStatus(epic);
    }

    protected void setAllTasks(List<Task> taskList, List<Epic> epicList, List<SubTask> subTaskList) {
        int id;

        for (Task task : taskList) {
            id = task.getId();
            tasks.put(id, task);
            if (id > currentId) {
                currentId = id;
            }
        }
        for (Epic epic : epicList) {
            id = epic.getId();
            epics.put(id, epic);
            if (id > currentId) {
                currentId = id;
            }
        }
        for (SubTask subTask : subTaskList) {
            id = subTask.getId();
            subTasks.put(id, subTask);
            epics.get(subTask.getEpicId()).addSubTaskId(id);
            if (id > currentId) {
                currentId = id;
            }
        }
        currentId++;
    }

    @Override
    public List<Task> getTaskList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpicList() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> getSubTaskList() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<SubTask> getEpicSubTaskList(int id) {
        if (!epics.containsKey(id)) {
            return null;
        }
        return epics.get(id).getSubTaskIdList().stream()
                .map(subTasks::get).collect(Collectors.toList());
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public void deleteAllTasks() {
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.remove(tasks.get(id));
        }
        tasks.keySet()
                .forEach(id -> {
                    historyManager.remove(id);
                    prioritizedTasks.remove(tasks.get(id));
                });
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        subTasks.keySet()
                .forEach(id -> {
                    historyManager.remove(id);
                    prioritizedTasks.remove(subTasks.get(id));
                });
        epics.keySet().forEach(historyManager::remove);
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        subTasks.keySet()
                .forEach(id -> {
                    historyManager.remove(id);
                    prioritizedTasks.remove(subTasks.get(id));
                });
        subTasks.clear();
        epics.values().forEach(Epic::clearSubTaskList);
    }

    @Override
    public Optional<Task> getTaskById(int id) {
        if (tasks.containsKey(id)) {
            historyManager.addTask(tasks.get(id));
        }
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public Optional<Epic> getEpicById(int id) {
        if (epics.containsKey(id)) {
            historyManager.addTask(epics.get(id));
        }
        return Optional.ofNullable(epics.get(id));
    }

    @Override
    public Optional<SubTask> getSubTaskById(int id) {
        if (subTasks.containsKey(id)) {
            historyManager.addTask(subTasks.get(id));
        }
        return Optional.ofNullable(subTasks.get(id));
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            Task oldTask = getTaskById(task.getId()).get();
            if (oldTask.getStartTime() != null) {
                prioritizedTasks.remove(oldTask);
            }
            tasks.put(task.getId(), task);
            if (task.getStartTime() != null) {
                if (checkTimeIntersections(task)) {
                    return;
                }
                prioritizedTasks.add(task);
            }
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
            SubTask oldTask = getSubTaskById(subTask.getId()).get();
            if (oldTask.getStartTime() != null) {
                prioritizedTasks.remove(oldTask);
            }
            subTasks.put(subTask.getId(), subTask);
            if (subTask.getStartTime() != null) {
                if (checkTimeIntersections(subTask)) {
                    return;
                }
                prioritizedTasks.add(subTask);
            }
            updateEpicStatus(epics.get(subTask.getEpicId()));
        }
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
        prioritizedTasks.remove(tasks.get(id));
    }

    @Override
    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            epics.get(id).getSubTaskIdList()
                    .forEach(i -> {
                        subTasks.remove(i);
                        historyManager.remove(i);
                        prioritizedTasks.remove(subTasks.get(i));
                    });
            epics.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubTaskById(int id) {
        if (subTasks.containsKey(id)) {
            Epic epic = epics.get(subTasks.get(id).getEpicId());

            epic.deleteSubTaskId(id);
            updateEpicStatus(epic);
            subTasks.remove(id);
            historyManager.remove(id);
            prioritizedTasks.remove(subTasks.get(id));
        }
    }

    private static boolean ifTasksIntersect(Task task1, Task task2) {
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime start2 = task2.getStartTime();
        return (start1.isAfter(start2) && start1.isBefore(task2.getEndTime()))
                || (start2.isAfter(start1) && start2.isBefore(task1.getEndTime()));
    }

    private boolean checkTimeIntersections(Task task) {
        return prioritizedTasks
                .stream()
                .anyMatch((Task task1) -> InMemoryTaskManager.ifTasksIntersect(task, task1));
    }

    public int getCurrentId() {
        return currentId;
    }

    @Override
    public Map<Integer, Task> getTasks() {
        return new HashMap<>(tasks);
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        return new HashMap<>(epics);
    }

    @Override
    public HashMap<Integer, SubTask> getSubTasks() {
        return new HashMap<>(subTasks);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public static void printAllTasks(TaskManager manager) {
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

    public static void printHistory(TaskManager manager) {
        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
        System.out.println("-".repeat(20));
    }

    public static void printPrioritizedTasks(InMemoryTaskManager manager) {
        System.out.println("Задачи по приоритету:");
        for (Task task : manager.getPrioritizedTasks()) {
            System.out.println(task);
        }
        System.out.println("-".repeat(20));
    }

}
