import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTaskIdList;
    private int numberOfNewTasks;
    private int numberOfDoneTasks;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        this.subTaskIdList = new ArrayList<>();
    }

    public Epic(String name, String description, int id) {
        super(name, description, id, TaskStatus.NEW);
        this.subTaskIdList = new ArrayList<>();
    }

    public Epic(String name, String description, ArrayList<Integer> subTaskIdList) {
        super(name, description, TaskStatus.NEW);
        this.subTaskIdList = subTaskIdList;
    }

    public Epic(String name, String description, int id, ArrayList<Integer> subTaskIdList) {
        super(name, description, id, TaskStatus.NEW);
        this.subTaskIdList = subTaskIdList;
    }

    public void clearSubTaskList() {
        subTaskIdList.clear();
        numberOfNewTasks = 0;
        numberOfDoneTasks = 0;
        this.setStatus(TaskStatus.NEW);
    }

    public ArrayList<Integer> getSubTaskIdList() {
        return subTaskIdList;
    }

    public void setNumberOfDoneTasks(int numberOfDoneTasks) {
        this.numberOfDoneTasks = numberOfDoneTasks;
    }

    public void setNumberOfNewTasks(int numberOfNewTasks) {
        this.numberOfNewTasks = numberOfNewTasks;
    }

    public void addNewTask() {
        numberOfNewTasks++;
    }

    public void deleteNewTask() {
        numberOfNewTasks--;
    }

    public void addDoneTask() {
        numberOfDoneTasks++;
    }

    public void deleteDoneTask() {
        numberOfDoneTasks--;
    }

    public void checkStatus() {
        if (numberOfNewTasks == subTaskIdList.size()) {
            this.setStatus(TaskStatus.NEW);
        } else if (numberOfDoneTasks == subTaskIdList.size()) {
            this.setStatus(TaskStatus.DONE);
        } else {
            this.setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}
