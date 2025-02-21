package task;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String name, String description, TaskStatus status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, int id, TaskStatus status, int epicId) {
        super(name, description, id, status);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, TaskStatus status, LocalDateTime startTime,
                   Duration duration, int epicId) {
        super(name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, int id, TaskStatus status, LocalDateTime startTime,
                   Duration duration, int epicId) {
        super(name, description, id, status, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}