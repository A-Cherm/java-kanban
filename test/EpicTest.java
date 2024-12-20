import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    public void shouldBeEqualEpicsWithTheSameId() {
        Epic epic1 = new Epic("a", "b", 1);
        Epic epic2 = new Epic("c", "d", 1);
        assertEquals(epic1, epic2, "Ошибка в равенстве экзепляров класса Epic");
    }

    @Test
    public void shouldAddAndDeleteSubtaskId() {
        Epic epic = new Epic("a", "b", 1);

        epic.addSubTaskId(2);

        ArrayList<Integer> subTaskId = epic.getSubTaskIdList();

        assertNotNull(subTaskId, "Список подзадач не возвращается");
        assertEquals(1, subTaskId.size(), "Неправильный размер списка подзадач эпика");

        epic.addSubTaskId(2);

        assertEquals(1, subTaskId.size(), "Неправильный размер списка подзадач эпика");

        epic.deleteSubTaskId(2);
        subTaskId = epic.getSubTaskIdList();

        assertEquals(0, subTaskId.size(), "Неправильный размер списка подзадач эпика");
    }

    @Test
    public void shouldClearSubtaskList() {
        Epic epic = new Epic("a", "b", 1);

        epic.addSubTaskId(2);
        epic.addSubTaskId(3);

        ArrayList<Integer> subTaskId = epic.getSubTaskIdList();

        assertNotNull(subTaskId, "Список подзадач не возвращается");
        assertEquals(2, subTaskId.size(), "Неправильный размер списка подзадач эпика");

        epic.clearSubTaskList();
        subTaskId = epic.getSubTaskIdList();

        assertEquals(0, subTaskId.size(), "Неправильный размер списка подзадач эпика");
    }
}