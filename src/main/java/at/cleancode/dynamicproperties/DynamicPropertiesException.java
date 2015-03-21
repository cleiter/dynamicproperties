package at.cleancode.dynamicproperties;

public class DynamicPropertiesException extends RuntimeException {
    public DynamicPropertiesException() {
    }

    public DynamicPropertiesException(Throwable cause) {
        super(cause);
    }

    public DynamicPropertiesException(String message) {
        super(message);
    }

    public DynamicPropertiesException(String message, Throwable cause) {
        super(message, cause);
    }
}
