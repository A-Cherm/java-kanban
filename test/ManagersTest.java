import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    @Test
    public void shouldInitializeDefaultManager() {
        TaskManager taskManager = Managers.getDefault();

        assertNotNull(taskManager.getTasks(), "Хэш-таблица задач не инициализирована");
        assertNotNull(taskManager.getEpics(), "Хэш-таблица эпиков не инициализирована");
        assertNotNull(taskManager.getSubTasks(), "Хэш-таблица подзадач не инициализирована");
        assertNotNull(taskManager.getHistoryManager(), "Менеджер истории не инициализирован");
    }

    @Test
    public void shouldInitializeDefaultHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager.getHistory(), "Список истории задач не инициализирован");
    }
}