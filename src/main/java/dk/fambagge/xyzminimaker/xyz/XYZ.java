package dk.fambagge.xyzminimaker.xyz;

import dk.fambagge.xyzminimaker.serial.SerialTransport;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XYZ {

    private final SerialTransport serial;

    public XYZ(String portName) {
        this.serial = new SerialTransport(portName, 115200);
        this.connect();
    }

    public synchronized XYZInfo queryAll() {
        this.connect();
        XYZInfo info = new XYZInfo();
        try {
            System.out.println("Query all info from printer...");
            String reply = this.serial.readAnswer("XYZv3/query=a\n", "$\n", 2500);
            Arrays.stream(reply.split("\n")).forEach(string -> {
                info.parse(string);
            }
            );
        } catch (Exception ex) {
            System.out.println("Error querying printer info: " + ex);
        }
        return info;
    }

    public synchronized void printFile(byte[] data) {
        this.connect();
        try {
            int remainingBytes = data.length;
            if (remainingBytes > 0) {
                System.out.println("Send upload command to printer... [" + remainingBytes + "]");
                this.serial.flush();
                String printCmd = "XYZv3/upload=temp.gcode," + remainingBytes + "\n";
                System.out.println("Sending cmd: [" + printCmd + "]");
                String reply = this.serial.readAnswer(printCmd, "ok\n", 2500);
                System.out.println("Upload start reply: " + reply);
                if (reply.trim().isEmpty()) {
                    System.out.println("We got reply, start upload...");
                    ByteArrayInputStream bin = new ByteArrayInputStream(data);
                    byte[] buffer = new byte[8192];
                    ByteBuffer byteBuffer = ByteBuffer.allocate(buffer.length + 12);
                    byteBuffer.order(ByteOrder.BIG_ENDIAN);
                    int blockCounter = 0;
                    while (remainingBytes > 0) {
                        int blockLength = bin.read(buffer);
                        byteBuffer.clear();
                        byteBuffer.putInt(blockCounter);
                        byteBuffer.putInt(blockLength);
                        byteBuffer.put(buffer, 0, blockLength);
                        byteBuffer.put((byte) 0);
                        byteBuffer.put((byte) 0);
                        byteBuffer.put((byte) 0);
                        byteBuffer.put((byte) 0);
                        byteBuffer.flip();
                        System.out.println("Sending block: " + blockCounter + " [" + byteBuffer.remaining() + "]");
                        this.serial.write(byteBuffer);
                        this.serial.flush();
                        reply = this.serial.readLine("ok\n", 5000);
                        System.out.println("Block reply: " + reply);
                        if (!reply.trim().isEmpty()) {
                            System.out.println("Error during block transfer: "+reply);
                            break;
                        }
                        remainingBytes -= blockLength;
                        ++blockCounter;
                    }
                    reply = this.serial.readAnswer("XYZv3/uploadDidFinish", "ok\n", 2500);
                    System.out.println("uploadDidFinish reply: " + reply);
                } else {
                    System.out.println("Non empty upload reply: "+reply);
                }
            } else {
                System.out.println("Encoded file was 0 bytes");
            }
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(XYZ.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cancel() {
        this.connect();
        System.out.println("Send printer cancel...");
        try {
            String string = this.serial.readAnswer("XYZv3/config=print[cancel]\n", "$\n", 2500);
            System.out.println("Printer cancel reply: [" + string + "]");
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(XYZ.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void pause() {
        this.connect();
        System.out.println("Send printer pause...");
        try {
            String reply = this.serial.readAnswer("XYZv3/config=print[pause]\n", "$\n", 2500);
            System.out.println("Printer pause reply: [" + reply + "]");
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(XYZ.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void resume() {
        this.connect();
        System.out.println("Send printer resume...");
        try {
            String reply = this.serial.readAnswer("XYZv3/config=print[resume]\n", "$\n", 2500);
            System.out.println("Printer resume reply: [" + reply + "]");
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(XYZ.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void home() {
        this.connect();
        try {
            String answer = this.serial.readAnswer("XYZv3/action=home\n", "$\n", 2500);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(XYZ.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void jog(int length, String axis, boolean oppositeDirection) {
        String dir = "+";
        if (oppositeDirection) {
            dir = "-";
        }
        try {
            String answer = this.serial.readAnswer("XYZv3/action=jog:{\"axis\":\"" + axis + "\",\"dir\":\"" + dir + "\",\"len\":\"" + length + "\"}\n", "$\n", 2500);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(XYZ.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void startFilamentLoad() {
        try {
            String answer = this.serial.readAnswer("XYZv3/action=load:new\n", "$\n", 2500);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(XYZ.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void stopFilamentLoad() {
        try {
            String answer = this.serial.readAnswer("XYZv3/action=load:cancel\n", "$\n", 2500);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(XYZ.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void startFilamentUnload() {
        try {
            String answer = this.serial.readAnswer("XYZv3/action=unload:new\n", "$\n", 2500);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(XYZ.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void stopFilamentUnload() {
        try {
            String answer = this.serial.readAnswer("XYZv3/action=unload:cancel\n", "$\n", 2500);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(XYZ.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public synchronized void destroy() {
        this.serial.close();
    }

    private void connect() {
        if (this.serial.isOpen()) {
            return;
        }
        try {
            this.serial.open(5000);
            try {
                System.out.println("Cleaning serial:");
                String line = "NAN";
                while (!line.isEmpty()) {
                    line = this.serial.readLine("\n", 1000);
                }
            } catch (Exception ex) {
            }
            System.out.println("Done!");
            System.out.println("Serial opened on port: " + this.serial.getPortIdentifier());
        } catch (IOException ex) {
            System.out.println("Unable to connect to printer: " + ex);
        }
    }
}
