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

    public XYZ(String string) {
        this.serial = new SerialTransport(string, 115200);
        this.connect();
    }

    public synchronized XYZInfo queryAll() {
        this.connect();
        XYZInfo xYZInfo = new XYZInfo();
        try {
            System.out.println("Query all info from printer...");
            String string2 = this.serial.readAnswer("XYZv3/query=a\n", "$\n", 2500);
            Arrays.stream(string2.split("\n")).forEach(string -> {
                xYZInfo.parse(string);
            }
            );
        } catch (Exception ex) {
            System.out.println("Error querying printer info: " + ex);
        }
        return xYZInfo;
    }

    public synchronized void printFile(byte[] arrby) {
        this.connect();
        try {
            int n = arrby.length;
            if (n > 0) {
                System.out.println("Send upload command to printer... [" + n + "]");
                this.serial.flush();
                String string = "XYZv3/upload=temp.gcode," + n + "\n";
                System.out.println("Sending cmd: [" + string + "]");
                String string2 = this.serial.readAnswer(string, "ok\n", 2500);
                System.out.println("Upload start reply: " + string2);
                if (string2.trim().isEmpty()) {
                    System.out.println("We got reply, start upload...");
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrby);
                    byte[] arrby2 = new byte[8192];
                    ByteBuffer byteBuffer = ByteBuffer.allocate(arrby2.length + 12);
                    byteBuffer.order(ByteOrder.BIG_ENDIAN);
                    int n2 = 0;
                    while (n > 0) {
                        int n3 = byteArrayInputStream.read(arrby2);
                        byteBuffer.clear();
                        byteBuffer.putInt(n2);
                        byteBuffer.putInt(n3);
                        byteBuffer.put(arrby2, 0, n3);
                        byteBuffer.put((byte) 0);
                        byteBuffer.put((byte) 0);
                        byteBuffer.put((byte) 0);
                        byteBuffer.put((byte) 0);
                        byteBuffer.flip();
                        System.out.println("Sending block: " + n2 + " [" + byteBuffer.remaining() + "]");
                        this.serial.write(byteBuffer);
                        this.serial.flush();
                        string2 = this.serial.readLine("ok\n", 5000);
                        System.out.println("Block reply: " + string2);
                        if (!string2.trim().isEmpty()) {
                            System.out.println("Error during block transfer...");
                            break;
                        }
                        n -= n3;
                        ++n2;
                    }
                    string2 = this.serial.readAnswer("XYZv3/uploadDidFinish", "ok\n", 2500);
                    System.out.println("uploadDidFinish reply: " + string2);
                } else {
                    System.out.println("Non empty reply!");
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
            String string = this.serial.readAnswer("XYZv3/config=print[pause]\n", "$\n", 2500);
            System.out.println("Printer pause reply: [" + string + "]");
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(XYZ.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void resume() {
        this.connect();
        System.out.println("Send printer resume...");
        try {
            String string = this.serial.readAnswer("XYZv3/config=print[resume]\n", "$\n", 2500);
            System.out.println("Printer resume reply: [" + string + "]");
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

    public void jog(int n, String axis, boolean bl) {
        String dir = "+";
        if (bl) {
            dir = "-";
        }
        try {
            String answer = this.serial.readAnswer("XYZv3/action=jog:{\"axis\":\"" + axis + "\",\"dir\":\"" + dir + "\",\"len\":\"" + n + "\"}\n", "$\n", 2500);
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
                String string = "NAN";
                while (!string.isEmpty()) {
                    string = this.serial.readLine("\n", 1000);
                }
            } catch (Exception ex) {
            }
            System.out.println("Done!");
            System.out.println("Serial opened on port: " + this.serial.getPortIdentifier());
        } catch (IOException var1_3) {
            System.out.println("Unable to connect to printer: " + var1_3);
        }
    }
}
