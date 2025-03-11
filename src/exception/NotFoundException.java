package exception;

public class NotFoundException extends RuntimeException {

    // исключение при отсутствии задачи с указанным id
    public NotFoundException(String message) {
        super(message);
    }
}
