package dk.fambagge.xyzminimaker.serial;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract interface TransportInterface
{
  public abstract void write(String paramString)
    throws IOException;
  
  public abstract void write(byte[] paramArrayOfByte)
    throws IOException;
  
  public abstract void write(byte paramByte, byte[] paramArrayOfByte)
    throws IOException;
  
  public abstract void write(byte[] paramArrayOfByte, int paramInt)
    throws IOException;
  
  public abstract void write(ByteBuffer paramByteBuffer)
    throws IOException;
  
  public abstract void write(ByteBuffer paramByteBuffer, int paramInt)
    throws IOException;
  
  public abstract int read(int paramInt)
    throws IOException, InterruptedException;
  
  public abstract int read(int paramInt, byte[] paramArrayOfByte)
    throws IOException, InterruptedException;
  
  public abstract String readLine(String paramString, int paramInt)
    throws IOException, InterruptedException;
  
  public abstract String readAnswer(String paramString1, String paramString2, int paramInt)
    throws IOException, InterruptedException;
  
  public abstract void flush()
    throws IOException;
  
  public abstract void close();
  
  public abstract void open(int paramInt)
    throws IOException, InterruptedException;
  
  public abstract boolean isOpen();
  
  public abstract String getGeneralName();
}


/* Location:              D:\Misc\Downloads\XYZPrint\XYZMinimaker-1.0-SNAPSHOT.jar!\dk\fambagge\xyzminimaker\serial\TransportInterface.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */