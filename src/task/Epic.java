package task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subTaskIdList;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        this.subTaskIdList = new ArrayList<>();
    }

    public Epic(String name, String description, int id) {
        super(name, description, id, TaskStatus.NEW);
        this.subTaskIdList = new ArrayList<>();
    }

    public void clearSubTaskList() {
        subTaskIdList.clear();
        this.setStatus(TaskStatus.NEW);
    }

    public ArrayList<Integer> getSubTaskIdList() {
        return new ArrayList<>(subTaskIdList);
    }

    public void addSubTaskId(int id) {
        if (!subTaskIdList.contains(id)) {
            subTaskIdList.add(id);
        }
    }

    public void deleteSubTaskId(int id) {
        for (int i = 0; i < subTaskIdList.size(); i++) {
            if (subTaskIdList.get(i) == id) {
                subTaskIdList.remove(i);
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
