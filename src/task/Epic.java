package task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtaskIdList;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        this.subtaskIdList = new ArrayList<>();
    }

    public Epic(String name, String description, int id) {
        super(name, description, id, TaskStatus.NEW);
        this.subtaskIdList = new ArrayList<>();
    }

    public void clearSubtaskList() {
        subtaskIdList.clear();
        this.setStatus(TaskStatus.NEW);
    }

    public ArrayList<Integer> getSubtaskIdList() {
        return new ArrayList<>(subtaskIdList);
    }

    public void addSubtaskId(int id) {
        if (!subtaskIdList.contains(id)) {
            subtaskIdList.add(id);
        }
    }

    public void deleteSubtaskId(int id) {
        for (int i = 0; i < subtaskIdList.size(); i++) {
            if (subtaskIdList.get(i) == id) {
                subtaskIdList.remove(i);
                return;
            }
        }
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }
}
