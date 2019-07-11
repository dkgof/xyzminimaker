/*    */ package dk.fambagge.xyzminimaker;
/*    */ 
/*    */ import java.util.Map;
/*    */ import java.util.Map.Entry;
/*    */ import org.eclipse.jetty.webapp.WebAppContext;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AliasEnhancedWebAppContext
/*    */   extends WebAppContext
/*    */ {
/*    */   public String getResourceAlias(String paramString)
/*    */   {
/* 19 */     Map<String,String> localMap = getResourceAliases();
/*    */     
/* 21 */     if (localMap == null) {
/* 22 */       return null;
/*    */     }
/*    */     
/*    */ 
/* 26 */     for (Entry localEntry : localMap.entrySet())
/*    */     {
/* 28 */       if (paramString.startsWith((String)localEntry.getKey())) {
/* 29 */         return paramString.replace((CharSequence)localEntry.getKey(), (CharSequence)localEntry.getValue());
/*    */       }
/*    */     }
/*    */     
/* 33 */     return null;
/*    */   }
/*    */ }


/* Location:              D:\Misc\Downloads\XYZPrint\XYZMinimaker-1.0-SNAPSHOT.jar!\dk\fambagge\xyzminimaker\AliasEnhancedWebAppContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */