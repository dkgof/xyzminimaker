package dk.fambagge.xyzminimaker.xyz;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class GcodeParser {

    public static class Gcode {

        public String getGcode() {
            return gcode;
        }

        public byte[] getXyzEncoded() {
            return xyzEncoded;
        }

        public int getFilamentUsed() {
            return filamentUsed;
        }

        public int getPrintTime() {
            return printTime;
        }

        public int getNumLayers() {
            return numLayers;
        }

        private final String gcode;
        private final byte[] xyzEncoded;
        private final int filamentUsed;
        private final int printTime;
        private final int numLayers;

        public Gcode(String gcode, byte[] xyzEncoded, int filamentUsed, int printTime, int numLayers) {
            this.gcode = gcode;
            this.xyzEncoded = xyzEncoded;
            this.filamentUsed = filamentUsed;
            this.printTime = printTime;
            this.numLayers = numLayers;
        }
    }

    public GcodeParser() {
    }

    public static Gcode parse(InputStream inputstream) {
        long start = System.currentTimeMillis();
        try {
            System.out.println("["+(System.currentTimeMillis() - start)+"ms] Parsing lines...");
            
            int printTime = 0;
            int layerCount = 0;
            int filamentUsed = 0;

            StringBuilder sbFile = new StringBuilder();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                line = line.trim();
                if (line.startsWith(";")) {
                    if (line.startsWith(";TIME:")) {
                        printTime = Integer.parseInt(line.substring(6));
                    } else if (line.startsWith(";LAYER_COUNT:")) {
                        layerCount = Integer.parseInt(line.substring(13));
                    } else if (line.startsWith(";Filament used:")) {
                        filamentUsed = (int) (Double.parseDouble(line.substring(15, line.length() - 1)) * 1000D);
                    }
                } else {
                    sbFile.append(line.replace("G0", "G1")).append("\n");
                }
            }
            
            StringBuilder sbHeader = new StringBuilder();
            sbHeader.append("; filename = temp.3w\n");
            sbHeader.append("; print_time  = ").append(printTime).append("\n");
            sbHeader.append("; machine = ").append(MINIMAKER_ID).append("\n");
            sbHeader.append("; total_layers  = ").append(layerCount).append("\n");
            sbHeader.append("; version   = 18020109\n");
            sbHeader.append("; total_filament   = ").append(filamentUsed).append("\n");
            String header = sbHeader.toString();
            String gcodeString = header + sbFile.toString();
            
            byte encryptedBytes1[] = null;
            byte encryptedBytes2[] = null;
            System.out.println("["+(System.currentTimeMillis() - start)+"ms] Encrypting header and gcode...");
            try {
                String keyString = "@xyzprinting.com";
                byte keyBytes[] = keyString.getBytes("UTF-8");
                byte ivBuffer[] = new byte[16];
                SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(1, keySpec, new IvParameterSpec(ivBuffer));
                byte gcodeBytes[] = gcodeString.getBytes();
                int gcodeLength = gcodeBytes.length;
                int gcodeLengthModulus16 = gcodeLength % 16;
                byte gcodePaddedBytes[] = new byte[gcodeLength + gcodeLengthModulus16];
                System.arraycopy(gcodeBytes, 0, gcodePaddedBytes, 0, gcodeLength);
                encryptedBytes1 = cipher.doFinal(gcodePaddedBytes);
            } catch (Exception exception) {
                System.out.println("Error during encryption: "+exception);
                exception.printStackTrace();
            }
            try {
                String keyString = "@xyzprinting.com@xyzprinting.com";
                byte keyBytes[] = keyString.getBytes("UTF-8");
                //byte ivBuffer[] = new byte[16];
                SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
                Cipher ciper = Cipher.getInstance("AES/ECB/PKCS5Padding");
                ciper.init(1, keySpec);
                byte gcodeBytes[] = gcodeString.getBytes();
                int gcodeLength = gcodeBytes.length;
                int gcodeLengthModulus16 = gcodeLength % 16;
                byte gcodePaddedBytes[] = new byte[gcodeLength + gcodeLengthModulus16];
                System.arraycopy(gcodeBytes, 0, gcodePaddedBytes, 0, gcodeLength);
                encryptedBytes2 = ciper.doFinal(gcodePaddedBytes);
            } catch (Exception exception) {
                System.out.println("Error during encryption: "+exception);
                exception.printStackTrace();
            }
            System.out.println("["+(System.currentTimeMillis() - start)+"ms] Writing file...");
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(bout);
            out.writeBytes(FILE_TYPE);
            out.write(1);
            out.write(2);
            out.write(0);
            out.write(0);
            out.writeInt(4684);
            for (int i = 0; i < 4684; i++) {
                out.write(0);
            }

            out.writeBytes("TagEJ256");
            out.writeInt(68);
            out.writeInt(calcXYZcrc32(encryptedBytes2));
            for (int i = 0; i < 64; i++) {
                out.write(0);
            }

            out.write(encryptedBytes1);
            int k1 = out.size();
            for (int i = 0; i < 8192 - k1; i++) {
                out.write(0);
            }

            out.write(encryptedBytes2);
            System.out.println("["+(System.currentTimeMillis() - start)+"ms] File convertion done...");
            return new Gcode(gcodeString, bout.toByteArray(), filamentUsed, printTime, layerCount);
        } catch (IOException ioexception) {
            Logger.getLogger(GcodeParser.class.getName()).log(Level.SEVERE, null, ioexception);
        }
        return null;
    }

    private static int calcXYZcrc32(byte[] data) {
        CRC32 crc32 = new CRC32();
        crc32.update(data);
        return (int) crc32.getValue();
    }

    private static final String FILE_TYPE = "3DPFNKG13WTW";
    private static final String MINIMAKER_ID = "dv1MX0A000";
}
