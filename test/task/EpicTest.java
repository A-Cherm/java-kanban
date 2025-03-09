package task;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    public void shouldBeEqualEpicsWithTheSameId() {
        Epic epic1 = new Epic("a", "b", 1);
        Epic epic2 = new Epic("c", "d", 1);
        assertEquals(epic1, epic2, "Ошибка в равенстве экзепляров класса task.Epic");
    }

    @Test
    public void shouldAddAndDeleteSubtaskId() {
        Epic epic = new Epic("a", "b", 1);

        epic.addSubtaskId(2);

        ArrayList<Integer> subTaskId = epic.getSubtaskIdList();

        assertNotNull(subTaskId, "Список подзадач не возвращается");
        assertEquals(1, subTaskId.size(), "Неправильный размер списка подзадач эпика");

        epic.addSubtaskId(2);

        assertEquals(1, subTaskId.size(), "Неправильный размер списка подзадач эпика");

        epic.deleteSubtaskId(2);
        subTaskId = epic.getSubtaskIdList();

        assertEquals(0, subTaskId.size(), "Неправильный размер списка подзадач эпика");
    }

    @Test
    public void shouldClearSubtaskList() {
        Epic epic = new Epic("a", "b", 1);

        epic.addSubtaskId(2);
        epic.addSubtaskId(3);

        ArrayList<Integer> subTaskId = epic.getSubtaskIdList();

        assertNotNull(subTaskId, "Список подзадач не возвращается");
        assertEquals(2, subTaskId.size(), "Неправильный размер списка подзадач эпика");

        epic.clearSubtaskList();
        subTaskId = epic.getSubtaskIdList();

        assertEquals(0, subTaskId.size(), "Неправильный размер списка подзадач эпика");
    }
}