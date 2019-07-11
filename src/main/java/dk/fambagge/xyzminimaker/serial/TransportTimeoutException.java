/*    */ package dk.fambagge.xyzminimaker.serial;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TransportTimeoutException
/*    */   extends IOException
/*    */ {
/*    */   public TransportTimeoutException(String paramString)
/*    */   {
/* 18 */     super(paramString);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public TransportTimeoutException(String paramString, Throwable paramThrowable)
/*    */   {
/* 27 */     super(paramString, paramThrowable);
/*    */   }
/*    */ }


/* Location:              D:\Misc\Downloads\XYZPrint\XYZMinimaker-1.0-SNAPSHOT.jar!\dk\fambagge\xyzminimaker\serial\TransportTimeoutException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */