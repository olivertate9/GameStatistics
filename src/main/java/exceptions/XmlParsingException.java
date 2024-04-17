package exceptions;

/**
 * Exception indicating an error occurred during XML parsing.
 */
public class XmlParsingException extends RuntimeException {

    public XmlParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
