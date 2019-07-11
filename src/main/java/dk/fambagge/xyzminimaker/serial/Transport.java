/*    */ package dk.fambagge.xyzminimaker.serial;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.ByteBuffer;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class Transport
/*    */   implements TransportInterface
/*    */ {
/* 14 */   protected static final Object globalOpenStateAtomicSync = new Object();
/* 15 */   protected final Object writeAtomicSync = new Object();
/* 16 */   protected final Object readAtomicSync = new Object();
/* 17 */   protected final Object localOpenStateAtomicSync = new Object();
/*    */   
/*    */ 
/*    */   public String getGeneralName()
/*    */   {
/* 22 */     return getClass().getSimpleName().toUpperCase().replaceAll("TRANSPORT", "");
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void write(String paramString)
/*    */     throws IOException
/*    */   {
/* 30 */     write(paramString.getBytes());
/*    */   }
/*    */   
/*    */   public void write(byte[] paramArrayOfByte)
/*    */     throws IOException
/*    */   {
/* 36 */     write(paramArrayOfByte, paramArrayOfByte.length);
/*    */   }
/*    */   
/*    */   public void write(byte paramByte, byte[] paramArrayOfByte)
/*    */     throws IOException
/*    */   {
/* 42 */     synchronized (this.writeAtomicSync) {
/* 43 */       write(new byte[] { paramByte });
/* 44 */       write(paramArrayOfByte, paramArrayOfByte.length);
/*    */     }
/*    */   }
/*    */   
/*    */   public void write(ByteBuffer paramByteBuffer)
/*    */     throws IOException
/*    */   {
/* 51 */     write(paramByteBuffer, paramByteBuffer.remaining());
/*    */   }
/*    */   
/*    */   public void write(ByteBuffer paramByteBuffer, int paramInt)
/*    */     throws IOException
/*    */   {
/* 57 */     byte[] arrayOfByte = new byte[paramByteBuffer.remaining()];
/* 58 */     paramByteBuffer.get(arrayOfByte);
/* 59 */     write(arrayOfByte);
/*    */   }
/*    */   
/*    */   public String readAnswer(String paramString1, String paramString2, int paramInt)
/*    */     throws IOException, InterruptedException
/*    */   {
/* 65 */     synchronized (this.writeAtomicSync) {
/* 66 */       synchronized (this.readAtomicSync) {
/* 67 */         write(paramString1);
/* 68 */         flush();
/* 69 */         return readLine(paramString2, paramInt);
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */   public String readLine(String paramString, int paramInt)
/*    */     throws IOException, InterruptedException
/*    */   {
/* 77 */     synchronized (this.readAtomicSync) {
/* 78 */       long l = System.nanoTime();
/* 79 */       StringBuilder localStringBuilder = new StringBuilder();
/* 80 */       while (System.nanoTime() - l < paramInt * 1000000L) {
/* 81 */         char c = (char)read(paramInt);
/*    */         
/* 83 */         localStringBuilder.append(c);
/*    */         
/* 85 */         int i = localStringBuilder.length() - paramString.length();
/* 86 */         if ((i >= 0) && (localStringBuilder.substring(i).equals(paramString))) {
/* 87 */           return localStringBuilder.toString().substring(0, i);
/*    */         }
/*    */       }
/* 90 */       return localStringBuilder.toString();
/*    */     }
/*    */   }
/*    */ }


/* Location:              D:\Misc\Downloads\XYZPrint\XYZMinimaker-1.0-SNAPSHOT.jar!\dk\fambagge\xyzminimaker\serial\Transport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */