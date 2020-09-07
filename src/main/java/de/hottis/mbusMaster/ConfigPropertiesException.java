package de.hottis.mbusMaster;

import java.io.IOException;

public class ConfigPropertiesException extends Exception {
    public ConfigPropertiesException() {
        super();
    }

    public ConfigPropertiesException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigPropertiesException(String message) {
        super(message);
    }

    public ConfigPropertiesException(Throwable cause) {
        super(cause);
    }
}