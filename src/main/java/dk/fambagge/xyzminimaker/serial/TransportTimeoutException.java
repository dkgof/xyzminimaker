package dk.fambagge.xyzminimaker.serial;

import java.io.IOException;

public class TransportTimeoutException
        extends IOException {

    public TransportTimeoutException(String msg) {
        super(msg);
    }

    public TransportTimeoutException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
