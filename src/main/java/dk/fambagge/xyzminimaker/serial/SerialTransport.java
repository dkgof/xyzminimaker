package dk.fambagge.xyzminimaker.serial;

import com.fazecast.jSerialComm.SerialPort;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class SerialTransport
        extends Transport {

    private String portIdentifier;
    private int baudrate;
    private int dataBits;
    private int stopBits;
    private int parity;
    private SerialPort port;
    private InputStream inputStream;
    private OutputStream outputStream;

    public static List<String> getAvailablePortIdentifiers() {
        ArrayList localArrayList = new ArrayList();
        for (SerialPort localSerialPort : SerialPort.getCommPorts()) {
            localArrayList.add(localSerialPort.getSystemPortName());
        }
        return localArrayList;
    }

    public SerialTransport() {
        this.baudrate = 115200;
        this.dataBits = 8;
        this.stopBits = 1;
        this.parity = 0;
    }

    public SerialTransport(String portName, int baudrate) {
        this();
        this.portIdentifier = portName;
        this.baudrate = baudrate;
    }

    public void setPortOptions(int baudrate, int dataBits, int stopBits, int parity) {
        if (isOpen()) {
            System.out.println("Setting port options on an already open port!");
        }

        this.baudrate = baudrate;
        this.dataBits = dataBits;
        this.stopBits = stopBits;
        this.parity = parity;
    }

    public void setBaudrate(int baudrate) {
        if (isOpen()) {
            System.out.println("Setting port options on an already open port!");
        }

        this.baudrate = baudrate;
    }

    @Override
    public void flush()
            throws IOException {
        if (this.outputStream != null) {
            this.outputStream.flush();
        }
    }

    @Override
    public void close() {
        synchronized (globalOpenStateAtomicSync) {
            long start = System.currentTimeMillis();
            if (!isOpen()) {
                return;
            }
            if (this.inputStream != null) {
                try {
                    this.inputStream.close();
                } catch (IOException ex) {
                }
                this.inputStream = null;
            }
            if (this.outputStream != null) {
                try {
                    this.outputStream.close();
                } catch (IOException ex) {
                }
                this.outputStream = null;
            }
            this.port.closePort();
            this.port = null;
            if (System.currentTimeMillis() - start > 50L) {
                System.out.println("Warning: Port close on " + this.portIdentifier + " took a long time: " + (System.currentTimeMillis() - start) + "ms");
            }
        }
    }

    @Override
    public void open(int timeout)
            throws IOException {
        synchronized (globalOpenStateAtomicSync) {

            long start = System.currentTimeMillis();
            if (isOpen()) {
                close();
            }

            if ((this.portIdentifier == null) || (this.portIdentifier.isEmpty()) || (this.portIdentifier.equals("null"))) {
                throw new IOException("Could not open non-identifier port " + this.portIdentifier);
            }
            SerialPort localSerialPort = SerialPort.getCommPort(this.portIdentifier);
            if (!localSerialPort.openPort()) {
                throw new IOException("Could not open port " + this.portIdentifier);
            }
            localSerialPort.setComPortTimeouts(0, 0, 0);
            localSerialPort.setComPortParameters(this.baudrate, this.dataBits, this.stopBits, this.parity);
            this.port = localSerialPort;
            this.inputStream = new BufferedInputStream(localSerialPort.getInputStream());
            this.outputStream = localSerialPort.getOutputStream();
            if (System.currentTimeMillis() - start > timeout) {
                System.out.println("Warning: Port open on " + this + " took a long time: " + (System.currentTimeMillis() - start) + "ms");
            }
        }
    }

    @Override
    public boolean isOpen() {
        synchronized (globalOpenStateAtomicSync) {
            return this.port != null;
        }
    }

    @Override
    public void write(byte[] data, int length)
            throws IOException {
        synchronized (this.writeAtomicSync) {
            if (this.outputStream != null) {
                this.outputStream.write(data, 0, length);
            }
        }
    }

    @Override
    public int read(int timeout)
            throws IOException, InterruptedException {
        synchronized (this.readAtomicSync) {
            long start = System.nanoTime();
            while (System.nanoTime() - start < timeout * 1000000L) {
                if (this.inputStream == null) {
                    throw new IOException("Read on non-connected transport aborted " + this);
                }
                if (this.inputStream.available() > 0) {
                    return this.inputStream.read();
                }
            }
            throw new TransportTimeoutException("Single-byte read timed out after " + (System.nanoTime() - start) + " nanosecs on '" + this + "'");
        }
    }

    @Override
    public int read(int timeout, byte[] data)
            throws IOException, InterruptedException {
        synchronized (this.readAtomicSync) {
            long start = System.nanoTime();
            while (System.nanoTime() - start < timeout * 1000000L) {
                if (this.inputStream == null) {
                    throw new IOException("Read on non-connected transport aborted " + this);
                }
                if (this.inputStream.available() > 0) {
                    return this.inputStream.read(data, 0, data.length);
                }
            }
            throw new TransportTimeoutException("Single-byte read timed out after " + (System.nanoTime() - start) + " nanosecs on '" + this + "'");
        }
    }

    public int available() throws IOException {
        synchronized (this.readAtomicSync) {
            if (this.inputStream == null) {
                throw new IOException("available called non-connected transport " + this);
            }
            return this.inputStream.available();
        }
    }

    public String getPortIdentifier() {
        return this.portIdentifier;
    }

    public void setPortIdentifier(String portIdentifier) {
        this.portIdentifier = portIdentifier;
    }

    @Override
    public String toString() {
        return "SerialTransport [" + this.portIdentifier + "@" + this.baudrate + ":" + this.dataBits + ":" + this.stopBits + ":" + this.parity + "]";
    }
}
