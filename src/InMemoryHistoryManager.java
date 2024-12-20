import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    public final static int HISTORY_SIZE = 10;
    private ArrayList<Task> taskHistory;

    public InMemoryHistoryManager() {
        taskHistory = new ArrayList<>();
    }

    @Override
    public void addTask(Task task) {
        if (taskHistory.size() == HISTORY_SIZE) {
            taskHistory.removeFirst();
        }
        taskHistory.add(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return taskHistory == null ? null : new ArrayList<>(taskHistory);
    }
}
