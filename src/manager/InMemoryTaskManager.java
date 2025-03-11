package manager;

import exception.NotFoundException;
import exception.TimeIntersectionException;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, Subtask> subtasks;
    private final Set<Task> prioritizedTasks;
    private final HistoryManager historyManager;
    protected static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private int currentId = 1;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
        prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    @Override
    public void addTask(Task task) {
        if (task.getStartTime() != null) {
            if (checkTimeIntersections(task)) {
                throw new TimeIntersectionException("Задача не добавлена, "
                        + "её время выполнения пересекается с уже существующей");
            }
            prioritizedTasks.add(task);
        }
        task.setId(currentId);
        tasks.put(currentId++, task);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(currentId);
        updateEpicStatusAndTime(epic);
        epics.put(currentId++, epic);
    }

    private void updateEpicStatusAndTime(Epic epic) {
        boolean isAllNewTasks = true;
        boolean isAllDoneTasks = true;
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        Duration epicDuration = Duration.ofMinutes(0);
        Subtask subtask;

        for (Integer i : epic.getSubtaskIdList()) {
            subtask = subtasks.get(i);

            if (subtask.getStatus() == TaskStatus.NEW) {
                isAllDoneTasks = false;
            } else if (subtask.getStatus() == TaskStatus.DONE) {
                isAllNewTasks = false;
            } else {
                isAllDoneTasks = false;
                isAllNewTasks = false;
            }
            if (subtask.getStartTime() != null) {
                if (startTime == null) {
                    startTime = subtask.getStartTime();
                    endTime = subtask.getEndTime();
                } else {
                    if (startTime.isAfter(subtask.getStartTime())) {
                        startTime = subtask.getStartTime();
                    }
                    if (endTime.isBefore(subtask.getEndTime())) {
                        endTime = subtask.getEndTime();
                    }
                }
                epicDuration = epicDuration.plus(subtask.getDuration());
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
    public void addSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getEpicId())) {
            throw new NotFoundException("Подзадача не добавлена, нет эпика с id = " + subtask.getEpicId());
        }
        if (subtask.getStartTime() != null) {
            if (checkTimeIntersections(subtask)) {
                throw new TimeIntersectionException("Подзадача не добавлена, "
                        + "её время выполнения пересекается с уже существующей");

            }
            prioritizedTasks.add(subtask);
        }
        Epic epic = epics.get(subtask.getEpicId());

        subtask.setId(currentId);
        epic.addSubtaskId(currentId);
        subtasks.put(currentId++, subtask);
        updateEpicStatusAndTime(epic);
    }

    protected void setAllTasks(List<Task> taskList, List<Epic> epicList, List<Subtask> subtaskList) {
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
        for (Subtask subtask : subtaskList) {
            id = subtask.getId();
            subtasks.put(id, subtask);
            epics.get(subtask.getEpicId()).addSubtaskId(id);
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
    public List<Subtask> getSubtaskList() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getEpicSubtaskList(int id) {
        if (!epics.containsKey(id)) {
            return null;
        }
        return epics.get(id).getSubtaskIdList().stream()
                .map(subtasks::get).collect(Collectors.toList());
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public void deleteAllTasks() {
        tasks.keySet()
                .forEach(id -> {
                    historyManager.remove(id);
                    prioritizedTasks.remove(tasks.get(id));
                });
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        subtasks.keySet()
                .forEach(id -> {
                    historyManager.remove(id);
                    prioritizedTasks.remove(subtasks.get(id));
                });
        epics.keySet().forEach(historyManager::remove);
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.keySet()
                .forEach(id -> {
                    historyManager.remove(id);
                    prioritizedTasks.remove(subtasks.get(id));
                });
        subtasks.clear();
        epics.values().forEach(Epic::clearSubtaskList);
    }

    @Override
    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            historyManager.addTask(tasks.get(id));
        } else {
            throw new NotFoundException("Нет задачи с id = " + id);
        }
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        if (epics.containsKey(id)) {
            historyManager.addTask(epics.get(id));
        } else {
            throw new NotFoundException("Нет эпика с id = " + id);
        }
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            historyManager.addTask(subtasks.get(id));
        } else {
            throw new NotFoundException("Нет подзадачи с id = " + id);
        }
        return subtasks.get(id);
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            if ((task.getStartTime() != null)
                && (checkTimeIntersections(task))) {
                    throw new TimeIntersectionException("Задача не обновлена, "
                            + "её время выполнения пересекается с уже существующей");
            }
            try {
                Task oldTask = getTaskById(task.getId());

                if (oldTask.getStartTime() != null) {
                    prioritizedTasks.remove(oldTask);
                }
            } catch (NotFoundException ignored) {
            }
            if (task.getStartTime() != null) {
                prioritizedTasks.add(task);
            }
            tasks.put(task.getId(), task);
        } else {
            throw new NotFoundException("Задача не обновлена, нет задачи с id = " + task.getId());
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic currentEpic = epics.get(epic.getId());

            currentEpic.setName(epic.getName());
            currentEpic.setDescription(epic.getDescription());
        } else {
            throw new NotFoundException("Эпик не обновлён, нет эпика с id = " + epic.getId());
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())
            && subtasks.get(subtask.getId()).getEpicId() == subtask.getEpicId()) {
            if ((subtask.getStartTime() != null)
                && (checkTimeIntersections(subtask))) {
                    throw new TimeIntersectionException("Подзадача не обновлена, "
                            + "её время выполнения пересекается с уже существующей");
            }
            try {
                Subtask oldTask = getSubtaskById(subtask.getId());

                if (oldTask.getStartTime() != null) {
                    prioritizedTasks.remove(oldTask);
                }
            } catch (NotFoundException ignored) {
            }
            if (subtask.getStartTime() != null) {
                prioritizedTasks.add(subtask);
            }
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatusAndTime(epics.get(subtask.getEpicId()));
        } else {
            throw new NotFoundException("Подзадача не обновлена, нет подзадачи с id = " + subtask.getId());
        }
    }

    @Override
    public void deleteTaskById(int id) {
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            epics.get(id).getSubtaskIdList()
                    .forEach(i -> {
                        prioritizedTasks.remove(subtasks.get(i));
                        subtasks.remove(i);
                        historyManager.remove(i);
                    });
            epics.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            Epic epic = epics.get(subtasks.get(id).getEpicId());

            epic.deleteSubtaskId(id);
            updateEpicStatusAndTime(epic);
            prioritizedTasks.remove(subtasks.get(id));
            subtasks.remove(id);
            historyManager.remove(id);
        }
    }

    private static boolean ifTasksIntersect(Task task1, Task task2) {
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime end2 = task2.getEndTime();
        return (start1.isAfter(start2) && start1.isBefore(end2))
                || (start2.isAfter(start1) && start2.isBefore(end1))
                || (start1.equals(start2))
                || (end1.equals(end2));
    }

    private boolean checkTimeIntersections(Task task) {
        return prioritizedTasks
                .stream()
                .filter(task1 -> !task.equals(task1))
                .anyMatch(task1 -> InMemoryTaskManager.ifTasksIntersect(task, task1));
    }

    @Override
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
    public HashMap<Integer, Subtask> getSubtasks() {
        return new HashMap<>(subtasks);
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

            for (Task task : manager.getEpicSubtaskList(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtaskList()) {
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
