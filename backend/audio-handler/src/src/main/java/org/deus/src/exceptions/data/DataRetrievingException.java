package org.deus.src.exceptions.data;

public class DataRetrievingException extends Exception {
    public DataRetrievingException() {
        super();
    }

    public DataRetrievingException(String message) {
        super(message);
    }

    public DataRetrievingException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataRetrievingException(Throwable cause) {
        super(cause);
    }
}
