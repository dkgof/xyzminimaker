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
    public void write(String msg)
            throws IOException {
        write(msg.getBytes());
    }

    @Override
    public void write(byte[] data)
            throws IOException {
        write(data, data.length);
    }

    @Override
    public void write(byte opcode, byte[] msg)
            throws IOException {
        synchronized (this.writeAtomicSync) {
            write(new byte[]{opcode});
            write(msg, msg.length);
        }
    }

    @Override
    public void write(ByteBuffer data)
            throws IOException {
        write(data, data.remaining());
    }

    @Override
    public void write(ByteBuffer data, int length)
            throws IOException {
        byte[] buffer = new byte[length];
        data.get(buffer);
        write(buffer);
    }

    @Override
    public String readAnswer(String request, String lineSep, int timeout)
            throws IOException, InterruptedException {
        synchronized (this.writeAtomicSync) {
            synchronized (this.readAtomicSync) {
                write(request);
                flush();
                return readLine(lineSep, timeout);
            }
        }
    }

    @Override
    public String readLine(String lineSep, int timeout)
            throws IOException, InterruptedException {
        synchronized (this.readAtomicSync) {
            long start = System.nanoTime();
            StringBuilder sb = new StringBuilder();
            while (System.nanoTime() - start < timeout * 1000000L) {
                char c = (char) read(timeout);

                sb.append(c);

                int currentRealLength  = sb.length() - lineSep.length();
                if ((currentRealLength  >= 0) && (sb.substring(currentRealLength ).equals(lineSep))) {
                    return sb.toString().substring(0, currentRealLength );
                }
            }
            return sb.toString();
        }
    }
}
