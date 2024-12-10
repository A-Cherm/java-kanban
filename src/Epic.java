import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTaskIdList;

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
}
