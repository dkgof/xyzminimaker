package dk.fambagge.xyzminimaker.serial;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface TransportInterface {

    public void write(String paramString)
            throws IOException;

    public void write(byte[] paramArrayOfByte)
            throws IOException;

    public void write(byte paramByte, byte[] paramArrayOfByte)
            throws IOException;

    public void write(byte[] paramArrayOfByte, int paramInt)
            throws IOException;

    public void write(ByteBuffer paramByteBuffer)
            throws IOException;

    public void write(ByteBuffer paramByteBuffer, int paramInt)
            throws IOException;

    public int read(int paramInt)
            throws IOException, InterruptedException;

    public int read(int paramInt, byte[] paramArrayOfByte)
            throws IOException, InterruptedException;

    public String readLine(String paramString, int paramInt)
            throws IOException, InterruptedException;

    public String readAnswer(String paramString1, String paramString2, int paramInt)
            throws IOException, InterruptedException;

    public void flush()
            throws IOException;

    public void close();

    public void open(int paramInt)
            throws IOException, InterruptedException;

    public boolean isOpen();

    public String getGeneralName();
}
