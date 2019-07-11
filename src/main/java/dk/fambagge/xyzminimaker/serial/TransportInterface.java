package dk.fambagge.xyzminimaker.serial;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface TransportInterface {

    public void write(String msg)
            throws IOException;

    public void write(byte[] data)
            throws IOException;

    public void write(byte opcode, byte[] data)
            throws IOException;

    public void write(byte[] data, int length)
            throws IOException;

    public void write(ByteBuffer data)
            throws IOException;

    public void write(ByteBuffer data, int length)
            throws IOException;

    public int read(int timeout)
            throws IOException, InterruptedException;

    public int read(int timeout, byte[] data)
            throws IOException, InterruptedException;

    public String readLine(String lineSep, int timeout)
            throws IOException, InterruptedException;

    public String readAnswer(String request, String lineSep, int timeout)
            throws IOException, InterruptedException;

    public void flush()
            throws IOException;

    public void close();

    public void open(int timeout)
            throws IOException, InterruptedException;

    public boolean isOpen();

    public String getGeneralName();
}
