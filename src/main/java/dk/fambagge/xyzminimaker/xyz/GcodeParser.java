// Decompiled by DJ v3.12.12.101 Copyright 2016 Atanas Neshkov  Date: 14-11-2018 12:48:37
// Home Page:  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   GcodeParser.java

package dk.fambagge.xyzminimaker.xyz;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class GcodeParser
{
    public static class Gcode
    {

        public String getGcode()
        {
            return gcode;
        }

        public byte[] getXyzEncoded()
        {
            return xyzEncoded;
        }

        public int getFilamentUsed()
        {
            return filamentUsed;
        }

        public int getPrintTime()
        {
            return printTime;
        }

        public int getNumLayers()
        {
            return numLayers;
        }

        private String gcode;
        private byte xyzEncoded[];
        private final int filamentUsed;
        private final int printTime;
        private final int numLayers;

        public Gcode(String s, byte abyte0[], int i, int j, int k)
        {
            gcode = s;
            xyzEncoded = abyte0;
            filamentUsed = i;
            printTime = j;
            numLayers = k;
        }
    }


    public GcodeParser()
    {
    }

    public static Gcode parse(InputStream inputstream)
    {
        long l = System.currentTimeMillis();
        try
        {
            System.out.println((new StringBuilder()).append("[").append(System.currentTimeMillis() - l).append("ms] Reading lines...").toString());
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream));
            ArrayList arraylist = new ArrayList();
            for(String s = bufferedreader.readLine(); s != null; s = bufferedreader.readLine())
                arraylist.add(s);

            int i = 0;
            int j = 0;
            int k = 0;
            StringBuilder stringbuilder = new StringBuilder();
            StringBuilder stringbuilder1 = new StringBuilder();
            System.out.println((new StringBuilder()).append("[").append(System.currentTimeMillis() - l).append("ms] Parsing lines...").toString());
            Object obj = arraylist.iterator();
            do
            {
                if(!((Iterator) (obj)).hasNext())
                    break;
                String s1 = (String)((Iterator) (obj)).next();
                s1 = s1.trim();
                if(s1.startsWith(";"))
                {
                    if(s1.startsWith(";TIME:"))
                        i = Integer.parseInt(s1.substring(6));
                    else
                    if(s1.startsWith(";LAYER_COUNT:"))
                        j = Integer.parseInt(s1.substring(13));
                    else
                    if(s1.startsWith(";Filament used:"))
                        k = (int)(Double.parseDouble(s1.substring(15, s1.length() - 1)) * 1000D);
                } else
                {
                    stringbuilder1.append(s1.replace("G0", "G1")).append("\n");
                }
            } while(true);
            stringbuilder.append("; filename = temp.3w\n");
            stringbuilder.append("; print_time  = ").append(i).append("\n");
            stringbuilder.append("; machine = dv1MX0A000\n");
            stringbuilder.append("; total_layers  = ").append(j).append("\n");
            stringbuilder.append("; version   = 18020109\n");
            stringbuilder.append("; total_filament   = ").append(k).append("\n");
            obj = stringbuilder.toString();
            String s2 = (new StringBuilder()).append(((String) (obj))).append(stringbuilder1.toString()).toString();
            byte abyte0[] = null;
            byte abyte1[] = null;
            System.out.println((new StringBuilder()).append("[").append(System.currentTimeMillis() - l).append("ms] Encrypting header and gcode...").toString());
            try
            {
                String s3 = "@xyzprinting.com";
                byte abyte2[] = s3.getBytes("UTF-8");
                byte abyte4[] = new byte[16];
                SecretKeySpec secretkeyspec = new SecretKeySpec(abyte2, "AES");
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(1, secretkeyspec, new IvParameterSpec(abyte4));
                byte abyte6[] = ((String) (obj)).getBytes();
                int i2 = abyte6.length;
                int k2 = i2 % 16;
                byte abyte8[] = new byte[i2 + k2];
                System.arraycopy(abyte6, 0, abyte8, 0, i2);
                abyte0 = cipher.doFinal(abyte8);
            }
            catch(Exception exception)
            {
                System.out.println((new StringBuilder()).append("Error during encryption: ").append(exception).toString());
                exception.printStackTrace();
            }
            try
            {
                String s4 = "@xyzprinting.com@xyzprinting.com";
                byte abyte3[] = s4.getBytes("UTF-8");
                byte abyte5[] = new byte[16];
                SecretKeySpec secretkeyspec1 = new SecretKeySpec(abyte3, "AES");
                Cipher cipher1 = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher1.init(1, secretkeyspec1);
                byte abyte7[] = s2.getBytes();
                int j2 = abyte7.length;
                int l2 = j2 % 16;
                byte abyte9[] = new byte[j2 + l2];
                System.arraycopy(abyte7, 0, abyte9, 0, j2);
                abyte1 = cipher1.doFinal(abyte9);
            }
            catch(Exception exception1)
            {
                System.out.println((new StringBuilder()).append("Error during encryption: ").append(exception1).toString());
                exception1.printStackTrace();
            }
            System.out.println((new StringBuilder()).append("[").append(System.currentTimeMillis() - l).append("ms] Writing file...").toString());
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
            DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
            dataoutputstream.writeBytes("3DPFNKG13WTW");
            dataoutputstream.write(1);
            dataoutputstream.write(2);
            dataoutputstream.write(0);
            dataoutputstream.write(0);
            dataoutputstream.writeInt(4684);
            for(int i1 = 0; i1 < 4684; i1++)
                dataoutputstream.write(0);

            dataoutputstream.writeBytes("TagEJ256");
            dataoutputstream.writeInt(68);
            dataoutputstream.writeInt(calcXYZcrc32(abyte1));
            for(int j1 = 0; j1 < 64; j1++)
                dataoutputstream.write(0);

            dataoutputstream.write(abyte0);
            int k1 = dataoutputstream.size();
            for(int l1 = 0; l1 < 8192 - k1; l1++)
                dataoutputstream.write(0);

            dataoutputstream.write(abyte1);
            System.out.println((new StringBuilder()).append("[").append(System.currentTimeMillis() - l).append("ms] File convertion done...").toString());
            return new Gcode(s2, bytearrayoutputstream.toByteArray(), k, i, j);
        }
        catch(IOException ioexception)
        {
            Logger.getLogger(GcodeParser.class.getName()).log(Level.SEVERE, null, ioexception);
        }
        return null;
    }

    private static int calcXYZcrc32(byte abyte0[])
    {
        CRC32 crc32 = new CRC32();
        crc32.update(abyte0);
        return (int)crc32.getValue();
    }

    private static final String FILE_TYPE = "3DPFNKG13WTW";
    private static final String MINIMAKER_ID = "dv1MX0A000";
}
