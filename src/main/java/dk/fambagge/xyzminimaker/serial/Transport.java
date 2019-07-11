package dk.fambagge.xyzminimaker.serial;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class Transport
        implements TransportInterface {

    protected static final Object globalOpenStateAtomicSync = new Object();
    protected final Object writeAtomicSync = new Object();
    protected final Object readAtomicSync = new Object();
    protected final Object localOpenStateAtomicSync = new Object();

    @Override
    public String getGeneralName() {
        return getClass().getSimpleName().toUpperCase().replaceAll("TRANSPORT", "");
    }

    @Override
    public void write(String paramString)
            throws IOException {
        write(paramString.getBytes());
    }

    @Override
    public void write(byte[] paramArrayOfByte)
            throws IOException {
        write(paramArrayOfByte, paramArrayOfByte.length);
    }

    @Override
    public void write(byte paramByte, byte[] paramArrayOfByte)
            throws IOException {
        synchronized (this.writeAtomicSync) {
            write(new byte[]{paramByte});
            write(paramArrayOfByte, paramArrayOfByte.length);
        }
    }

    @Override
    public void write(ByteBuffer paramByteBuffer)
            throws IOException {
        write(paramByteBuffer, paramByteBuffer.remaining());
    }

    @Override
    public void write(ByteBuffer paramByteBuffer, int paramInt)
            throws IOException {
        byte[] arrayOfByte = new byte[paramByteBuffer.remaining()];
        paramByteBuffer.get(arrayOfByte);
        write(arrayOfByte);
    }

    @Override
    public String readAnswer(String paramString1, String paramString2, int paramInt)
            throws IOException, InterruptedException {
        synchronized (this.writeAtomicSync) {
            synchronized (this.readAtomicSync) {
                write(paramString1);
                flush();
                return readLine(paramString2, paramInt);
            }
        }
    }

    public String readLine(String paramString, int paramInt)
            throws IOException, InterruptedException {
        synchronized (this.readAtomicSync) {
            long l = System.nanoTime();
            StringBuilder localStringBuilder = new StringBuilder();
            while (System.nanoTime() - l < paramInt * 1000000L) {
                char c = (char) read(paramInt);

                localStringBuilder.append(c);

                int i = localStringBuilder.length() - paramString.length();
                if ((i >= 0) && (localStringBuilder.substring(i).equals(paramString))) {
                    return localStringBuilder.toString().substring(0, i);
                }
            }
            return localStringBuilder.toString();
        }
    }
}
