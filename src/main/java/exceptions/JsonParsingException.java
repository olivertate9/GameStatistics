package exceptions;

/**
 * Exception indicating an error occurred during JSON parsing.
 */
public class JsonParsingException extends RuntimeException {

    public JsonParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
