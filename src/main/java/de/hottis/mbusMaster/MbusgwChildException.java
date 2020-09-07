package de.hottis.mbusMaster;

import java.io.IOException;

public class MbusgwChildException extends MbusException {
    public MbusgwChildException() {
        super();
    }

    public MbusgwChildException(String message, Throwable cause) {
        super(message, cause);
    }

    public MbusgwChildException(String message) {
        super(message);
    }

    public MbusgwChildException(Throwable cause) {
        super(cause);
    }
}