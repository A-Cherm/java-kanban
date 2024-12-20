import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    public void shouldBeEqualTasksWithTheSameId() {
        Task task1 = new Task("a", "b", 1, TaskStatus.NEW);
        Task task2 = new Task("c", "d", 1, TaskStatus.DONE);
        assertEquals(task1, task2, "Ошибка в равенстве экзепляров класса Task");
    }

}