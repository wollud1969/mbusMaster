package de.hottis.mbusMaster;

import java.io.IOException;

public class MbusException extends IOException {
    public MbusException() {
        super();
    }

    public MbusException(String message, Throwable cause) {
        super(message, cause);
    }

    public MbusException(String message) {
        super(message);
    }

    public MbusException(Throwable cause) {
        super(cause);
    }
}