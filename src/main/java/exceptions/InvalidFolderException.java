package exceptions;

/**
 * Exception indicating that an invalid folder was provided.
 */
public class InvalidFolderException extends RuntimeException {

    public InvalidFolderException(String message) {
        super(message);
    }
}
