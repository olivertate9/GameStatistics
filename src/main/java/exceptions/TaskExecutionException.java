package exceptions;

/**
 * Exception indicating an error occurred during task execution.
 */
public class TaskExecutionException extends RuntimeException {

    public TaskExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
