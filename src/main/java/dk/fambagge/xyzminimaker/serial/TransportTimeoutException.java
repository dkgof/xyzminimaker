package dk.fambagge.xyzminimaker.serial;

import java.io.IOException;

public class TransportTimeoutException
        extends IOException {

    public TransportTimeoutException(String paramString) {
        super(paramString);
    }

    public TransportTimeoutException(String paramString, Throwable paramThrowable) {
        super(paramString, paramThrowable);
    }
}
