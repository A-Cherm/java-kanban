package exception;

public class TimeIntersectionException extends RuntimeException {

    // исключение при пересечении времени выполнения новой задачи с уже существующими
    public TimeIntersectionException(String message) {
        super(message);
    }
}
