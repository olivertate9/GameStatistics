package exceptions;

/**
 * Exception indicating that an invalid attribute was provided.
 */
public class InvalidAttributeException extends RuntimeException {

    public InvalidAttributeException(String message) {
        super(message);
    }
}
