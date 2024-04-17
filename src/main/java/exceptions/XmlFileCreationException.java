package exceptions;

/**
 * Exception indicating an error occurred during XML file creation.
 */
public class XmlFileCreationException extends RuntimeException {

    public XmlFileCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
