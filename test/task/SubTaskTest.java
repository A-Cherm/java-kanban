package task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    @Test
    public void shouldBeEqualSubTasksWithTheSameId() {
        Task SubTask1 = new SubTask("a", "b", 1, TaskStatus.NEW, 2);
        Task SubTask2 = new SubTask("c", "d", 1, TaskStatus.DONE, 3);
        assertEquals(SubTask1, SubTask2, "Ошибка в равенстве экзепляров класса task.Task");
    }

}