package task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    public void shouldBeEqualSubtasksWithTheSameId() {
        Task Subtask1 = new Subtask("a", "b", 1, TaskStatus.NEW, 2);
        Task Subtask2 = new Subtask("c", "d", 1, TaskStatus.DONE, 3);
        assertEquals(Subtask1, Subtask2, "Ошибка в равенстве экзепляров класса task.Task");
    }

}