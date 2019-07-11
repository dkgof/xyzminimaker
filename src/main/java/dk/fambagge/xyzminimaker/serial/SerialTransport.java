/*     */ package dk.fambagge.xyzminimaker.serial;
/*     */ 
/*     */ import com.fazecast.jSerialComm.SerialPort;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SerialTransport
/*     */   extends Transport
/*     */ {
/*     */   private String portIdentifier;
/*     */   private int baudrate;
/*     */   private int dataBits;
/*     */   private int stopBits;
/*     */   private int parity;
/*     */   private SerialPort port;
/*     */   private InputStream inputStream;
/*     */   private OutputStream outputStream;
/*  27 */   private boolean useBusyWaitPolling = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static List<String> getAvailablePortIdentifiers()
/*     */   {
/*  35 */     ArrayList localArrayList = new ArrayList();
/*  36 */     for (SerialPort localSerialPort : SerialPort.getCommPorts()) {
/*  37 */       localArrayList.add(localSerialPort.getSystemPortName());
/*     */     }
/*  39 */     return localArrayList;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public SerialTransport()
/*     */   {
/*  47 */     this.baudrate = 115200;
/*  48 */     this.dataBits = 8;
/*  49 */     this.stopBits = 1;
/*  50 */     this.parity = 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SerialTransport(String paramString, int paramInt)
/*     */   {
/*  62 */     this();
/*  63 */     this.portIdentifier = paramString;
/*  64 */     this.baudrate = paramInt;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPortOptions(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/*  77 */     if (isOpen()) {
/*  78 */       System.out.println("Setting port options on an already open port!");
/*     */     }
/*     */     
/*  81 */     this.baudrate = paramInt1;
/*  82 */     this.dataBits = paramInt2;
/*  83 */     this.stopBits = paramInt3;
/*  84 */     this.parity = paramInt4;
/*     */   }
/*     */   
/*     */   public void setBaudrate(int paramInt) {
/*  88 */     if (isOpen()) {
/*  89 */       System.out.println("Setting port options on an already open port!");
/*     */     }
/*     */     
/*  92 */     this.baudrate = paramInt;
/*     */   }
/*     */   
/*     */   public void flush()
/*     */     throws IOException
/*     */   {
/*  98 */     if (this.outputStream != null) {
/*  99 */       this.outputStream.flush();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void close()
/*     */   {
/* 106 */     synchronized (globalOpenStateAtomicSync) {
/* 107 */       long l = System.currentTimeMillis();
/* 108 */       if (!isOpen()) {
/* 109 */         return;
/*     */       }
/* 111 */       if (this.inputStream != null) {
/*     */         try {
/* 113 */           this.inputStream.close();
/*     */         }
/*     */         catch (IOException localIOException1) {}
/* 116 */         this.inputStream = null;
/*     */       }
/* 118 */       if (this.outputStream != null) {
/*     */         try {
/* 120 */           this.outputStream.close();
/*     */         }
/*     */         catch (IOException localIOException2) {}
/* 123 */         this.outputStream = null;
/*     */       }
/* 125 */       this.port.closePort();
/* 126 */       this.port = null;
/* 127 */       if (System.currentTimeMillis() - l > 50L) {
/* 128 */         System.out.println("Warning: Port close on " + this.portIdentifier + " took a long time: " + (System.currentTimeMillis() - l) + "ms");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void open(int paramInt)
/*     */     throws IOException
/*     */   {
/* 136 */     synchronized (globalOpenStateAtomicSync)
/*     */     {
/*     */ 
/*     */ 
/* 140 */       long l = System.currentTimeMillis();
/* 141 */       if (isOpen()) {
/* 142 */         close();
/*     */       }
/*     */       
/* 145 */       if ((this.portIdentifier == null) || (this.portIdentifier.isEmpty()) || (this.portIdentifier.equals("null"))) {
/* 146 */         throw new IOException("Could not open non-identifier port " + this.portIdentifier);
/*     */       }
/* 148 */       SerialPort localSerialPort = SerialPort.getCommPort(this.portIdentifier);
/* 149 */       if (!localSerialPort.openPort()) {
/* 150 */         throw new IOException("Could not open port " + this.portIdentifier);
/*     */       }
/* 152 */       localSerialPort.setComPortTimeouts(0, 0, 0);
/* 153 */       localSerialPort.setComPortParameters(this.baudrate, this.dataBits, this.stopBits, this.parity);
/* 154 */       this.port = localSerialPort;
/* 155 */       this.inputStream = new BufferedInputStream(localSerialPort.getInputStream());
/* 156 */       this.outputStream = localSerialPort.getOutputStream();
/* 157 */       if (System.currentTimeMillis() - l > paramInt) {
/* 158 */         System.out.println("Warning: Port open on " + this + " took a long time: " + (System.currentTimeMillis() - l) + "ms");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isOpen()
/*     */   {
/* 167 */     synchronized (globalOpenStateAtomicSync) {
/* 168 */       return this.port != null;
/*     */     }
/*     */   }
/*     */   
/*     */   public void write(byte[] paramArrayOfByte, int paramInt)
/*     */     throws IOException
/*     */   {
/* 175 */     synchronized (this.writeAtomicSync) {
/* 176 */       if (this.outputStream != null) {
/* 177 */         this.outputStream.write(paramArrayOfByte, 0, paramInt);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public int read(int paramInt)
/*     */     throws IOException, InterruptedException
/*     */   {
/* 185 */     synchronized (this.readAtomicSync) {
/* 186 */       long l = System.nanoTime();
/* 187 */       while (System.nanoTime() - l < paramInt * 1000000L) {
/* 188 */         if (this.inputStream == null) {
/* 189 */           throw new IOException("Read on non-connected transport aborted " + this);
/*     */         }
/* 191 */         if (this.inputStream.available() > 0) {
/* 192 */           return this.inputStream.read();
/*     */         }
/* 194 */         if (!this.useBusyWaitPolling) {
/* 195 */           Thread.sleep(1L);
/*     */         }
/*     */       }
/* 198 */       throw new TransportTimeoutException("Single-byte read timed out after " + (System.nanoTime() - l) + " nanosecs on '" + this + "'");
/*     */     }
/*     */   }
/*     */   
/*     */   public int read(int paramInt, byte[] paramArrayOfByte)
/*     */     throws IOException, InterruptedException
/*     */   {
/* 205 */     synchronized (this.readAtomicSync) {
/* 206 */       long l = System.nanoTime();
/* 207 */       while (System.nanoTime() - l < paramInt * 1000000L) {
/* 208 */         if (this.inputStream == null) {
/* 209 */           throw new IOException("Read on non-connected transport aborted " + this);
/*     */         }
/* 211 */         if (this.inputStream.available() > 0) {
/* 212 */           return this.inputStream.read(paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */         }
/* 214 */         if (!this.useBusyWaitPolling) {
/* 215 */           Thread.sleep(1L);
/*     */         }
/*     */       }
/* 218 */       throw new TransportTimeoutException("Single-byte read timed out after " + (System.nanoTime() - l) + " nanosecs on '" + this + "'");
/*     */     }
/*     */   }
/*     */   
/*     */   public int available() throws IOException {
/* 223 */     synchronized (this.readAtomicSync) {
/* 224 */       if (this.inputStream == null) {
/* 225 */         throw new IOException("available called non-connected transport " + this);
/*     */       }
/* 227 */       return this.inputStream.available();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getPortIdentifier()
/*     */   {
/* 237 */     return this.portIdentifier;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPortIdentifier(String paramString)
/*     */   {
/* 247 */     this.portIdentifier = paramString;
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 253 */     return "SerialTransport [" + this.portIdentifier + "@" + this.baudrate + ":" + this.dataBits + ":" + this.stopBits + ":" + this.parity + "]";
/*     */   }
/*     */ }


/* Location:              D:\Misc\Downloads\XYZPrint\XYZMinimaker-1.0-SNAPSHOT.jar!\dk\fambagge\xyzminimaker\serial\SerialTransport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */