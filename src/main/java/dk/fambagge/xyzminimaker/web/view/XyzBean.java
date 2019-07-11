/*     */ package dk.fambagge.xyzminimaker.web.view;
/*     */ 
/*     */ import dk.fambagge.xyzminimaker.xyz.GcodeParser;
/*     */ import dk.fambagge.xyzminimaker.xyz.GcodeParser.Gcode;
/*     */ import dk.fambagge.xyzminimaker.xyz.XYZ;
/*     */ import dk.fambagge.xyzminimaker.xyz.XYZInfo;
/*     */ import dk.fambagge.xyzminimaker.xyz.XYZInfo.PrintStatus;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.Serializable;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.annotation.PostConstruct;
/*     */ import javax.enterprise.context.ApplicationScoped;
/*     */ import javax.faces.component.UIComponent;
/*     */ import javax.inject.Named;
/*     */ import org.primefaces.component.tabview.Tab;
/*     */ import org.primefaces.component.tabview.TabView;
/*     */ import org.primefaces.event.FileUploadEvent;
/*     */ import org.primefaces.event.TabChangeEvent;
/*     */ import org.primefaces.model.UploadedFile;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Named
/*     */ @ApplicationScoped
/*     */ public class XyzBean
/*     */   implements Serializable
/*     */ {
/*     */   private XYZ xyz;
/*     */   private long lastInfoCacheTimestamp;
/*     */   private XYZInfo cachedInfo;
/*     */   private GcodeParser.Gcode currentGcode;
/*     */   private double jogAmount;
/*     */   private int currentTab;
/*     */   
/*     */   @PostConstruct
/*     */   private void init()
/*     */   {
/*  45 */     System.out.println("Starting XYZ service!");
/*  46 */     this.xyz = new XYZ("COM3");
/*  47 */     this.lastInfoCacheTimestamp = -1L;
/*  48 */     this.currentGcode = null;
/*  49 */     this.jogAmount = 0.1D;
/*  50 */     this.currentTab = 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public XYZInfo getInfo()
/*     */   {
/*  56 */     if ((this.cachedInfo == null) || (System.currentTimeMillis() - this.lastInfoCacheTimestamp > 2000L)) {
/*  57 */       this.cachedInfo = this.xyz.queryAll();
/*  58 */       this.lastInfoCacheTimestamp = System.currentTimeMillis();
/*     */     }
/*     */     
/*  61 */     return this.cachedInfo;
/*     */   }
/*     */   
/*     */   public void handleUpload(FileUploadEvent event) {
/*  65 */     System.out.println("Upload: " + event.getFile().getFileName());
/*     */     try
/*     */     {
/*  68 */       this.currentGcode = GcodeParser.parse(event.getFile().getInputstream());
/*     */       
/*  70 */       System.out.println("Ready!");
/*     */     }
/*     */     catch (IOException ex)
/*     */     {
/*  74 */       Logger.getLogger(XyzBean.class.getName()).log(Level.SEVERE, null, ex);
/*     */     }
/*     */   }
/*     */   
/*     */   public void onTabChange(TabChangeEvent evt) {
/*  79 */     Tab targetTab = evt.getTab();
/*  80 */     TabView view = (TabView)targetTab.getParent();
/*     */     
/*  82 */     int clientActiveIndex = 0;
/*     */     
/*  84 */     for (UIComponent tab : view.getChildren()) {
/*  85 */       if (tab.equals(targetTab))
/*     */         break;
/*  87 */       if (tab.isRendered()) {
/*  88 */         clientActiveIndex++;
/*     */       }
/*     */     }
/*     */     
/*  92 */     this.currentTab = clientActiveIndex;
/*     */     
/*  94 */     System.out.println("Active tab: " + this.currentTab);
/*     */   }
/*     */   
/*     */   public void print() {
/*  98 */     if (this.currentGcode != null) {
/*  99 */       this.xyz.printFile(this.currentGcode.getXyzEncoded());
/*     */     }
/*     */   }
/*     */   
/*     */   public void cancel() {
/* 104 */     if (isPrinting()) {
/* 105 */       this.xyz.cancel();
/*     */     }
/*     */   }
/*     */   
/*     */   public String getGcodeFormatted() {
/* 110 */     if (this.currentGcode != null) {
/* 111 */       return this.currentGcode.getGcode().replace("\n", ";N;");
/*     */     }
/* 113 */     return "";
/*     */   }
/*     */   
/*     */ 
/*     */   public String getPrintTimeFormatted()
/*     */   {
/* 119 */     if (this.currentGcode != null) {
/* 120 */       int timeSeconds = this.currentGcode.getPrintTime();
/*     */       
/* 122 */       int timeMinutes = timeSeconds / 60;
/* 123 */       timeSeconds -= timeMinutes * 60;
/*     */       
/* 125 */       int timeHours = timeMinutes / 60;
/* 126 */       timeMinutes -= timeHours * 60;
/*     */       
/* 128 */       return pad(timeHours) + ":" + pad(timeMinutes) + ":" + pad(timeSeconds);
/*     */     }
/* 130 */     return "";
/*     */   }
/*     */   
/*     */   private String pad(int i)
/*     */   {
/* 135 */     String s = "" + i;
/* 136 */     if (s.length() == 1) {
/* 137 */       s = "0" + s;
/*     */     }
/*     */     
/* 140 */     return s;
/*     */   }
/*     */   
/*     */   public String getFilamentUseFormatted() {
/* 144 */     if (this.currentGcode != null) {
/* 145 */       return String.format("%.2f", new Object[] { Double.valueOf(this.currentGcode.getFilamentUsed() / 1000.0D) });
/*     */     }
/* 147 */     return "";
/*     */   }
/*     */   
/*     */   public String getFilamentPriceFormatted()
/*     */   {
/* 152 */     if (this.currentGcode != null) {
/* 153 */       double usedFilamentM = this.currentGcode.getFilamentUsed() / 1000.0D;
/*     */       
/* 155 */       double pricePerM = 0.5D;
/*     */       
/* 157 */       return String.format("%.2f", new Object[] { Double.valueOf(usedFilamentM * pricePerM) });
/*     */     }
/* 159 */     return "";
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isIdle()
/*     */   {
/* 165 */     getInfo();
/*     */     
/* 167 */     return this.cachedInfo.getPrintStatus() == XYZInfo.PrintStatus.PRINT_NONE;
/*     */   }
/*     */   
/*     */   public boolean isPrinting()
/*     */   {
/* 172 */     getInfo();
/*     */     
/* 174 */     switch (this.cachedInfo.getPrintStatus()) {
/*     */     case PRINTING_IN_PROGRESS: 
/*     */     case PRINT_HEATING: 
/*     */     case PRINT_INITIAL: 
/* 178 */       return true;
/*     */     }
/*     */     
/* 181 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isPausable()
/*     */   {
/* 186 */     getInfo();
/*     */     
/* 188 */     switch (this.cachedInfo.getPrintStatus()) {
/*     */     case PRINTING_IN_PROGRESS: 
/* 190 */       return true;
/*     */     }
/*     */     
/* 193 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isPaused() {
/* 197 */     getInfo();
/*     */     
/* 199 */     return this.cachedInfo.getPrintStatus() == XYZInfo.PrintStatus.STATE_PRINT_PAUSE;
/*     */   }
/*     */   
/*     */   public boolean isJogMode() {
/* 203 */     getInfo();
/*     */     
/* 205 */     switch (this.cachedInfo.getPrintStatus()) {
/*     */     case PRINT_JOG_MODE: 
/*     */     case STATE_PRINT_JOG_MODE: 
/* 208 */       return true;
/*     */     }
/*     */     
/* 211 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isShowPrintControls()
/*     */   {
/* 216 */     getInfo();
/*     */     
/* 218 */     switch (this.cachedInfo.getPrintStatus()) {
/*     */     case PRINTING_IN_PROGRESS: 
/*     */     case PRINT_HEATING: 
/*     */     case PRINT_INITIAL: 
/*     */     case PRINT_NONE: 
/* 223 */       return true;
/*     */     }
/*     */     
/* 226 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isShowJogControls()
/*     */   {
/* 231 */     getInfo();
/*     */     
/* 233 */     switch (this.cachedInfo.getPrintStatus()) {
/*     */     case PRINT_JOG_MODE: 
/*     */     case STATE_PRINT_JOG_MODE: 
/*     */     case PRINT_NONE: 
/*     */     case STATE_PRINT_HOMING: 
/* 238 */       return true;
/*     */     }
/*     */     
/* 241 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isShowFilamentControls()
/*     */   {
/* 246 */     getInfo();
/*     */     
/* 248 */     switch (this.cachedInfo.getPrintStatus()) {
/*     */     case PRINT_NONE: 
/*     */     case STATE_PRINT_LOAD_FILAMENT: 
/*     */     case STATE_PRINT_UNLOAD_FILAMENT: 
/* 252 */       return true;
/*     */     }
/*     */     
/* 255 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isShowCalibrateControls()
/*     */   {
/* 260 */     getInfo();
/*     */     
/* 262 */     switch (this.cachedInfo.getPrintStatus()) {
/*     */     case PRINT_NONE: 
/* 264 */       return true;
/*     */     }
/*     */     
/* 267 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isFilamentLoadPossible()
/*     */   {
/* 272 */     getInfo();
/*     */     
/* 274 */     switch (this.cachedInfo.getPrintStatus()) {
/*     */     case PRINT_NONE: 
/*     */     case STATE_PRINT_LOAD_FILAMENT: 
/* 277 */       return true;
/*     */     }
/*     */     
/* 280 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isFilamentUnloadPossible() {
/* 284 */     getInfo();
/*     */     
/* 286 */     switch (this.cachedInfo.getPrintStatus()) {
/*     */     case PRINT_NONE: 
/*     */     case STATE_PRINT_UNLOAD_FILAMENT: 
/* 289 */       return true;
/*     */     }
/*     */     
/* 292 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public GcodeParser.Gcode getCurrentGcode()
/*     */   {
/* 299 */     return this.currentGcode;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public double getJogAmount()
/*     */   {
/* 306 */     return this.jogAmount;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setJogAmount(double jogAmount)
/*     */   {
/* 313 */     this.jogAmount = jogAmount;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getCurrentTab()
/*     */   {
/* 320 */     return this.currentTab;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setCurrentTab(int currentTab)
/*     */   {
/* 327 */     this.currentTab = currentTab;
/*     */   }
/*     */   
/*     */   public void doHome() {
/* 331 */     this.xyz.home();
/*     */   }
/*     */   
/*     */   public void doJog(String axis, boolean negative) {
/* 335 */     int amount = (int)(this.jogAmount * 1000.0D);
/*     */     
/* 337 */     if ((!axis.equals("x")) && (!axis.equals("y")) && (!axis.equals("z"))) {
/* 338 */       System.out.println("Axis has to be one of x|y|z ...");
/* 339 */       return;
/*     */     }
/*     */     
/* 342 */     this.xyz.jog(amount, axis, negative);
/*     */   }
/*     */   
/*     */   public boolean isFilamentLoading() {
/* 346 */     getInfo();
/*     */     
/* 348 */     switch (this.cachedInfo.getPrintStatus()) {
/*     */     case STATE_PRINT_LOAD_FILAMENT: 
/* 350 */       return true;
/*     */     }
/*     */     
/* 353 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isFilamentUnloading() {
/* 357 */     getInfo();
/*     */     
/* 359 */     switch (this.cachedInfo.getPrintStatus()) {
/*     */     case STATE_PRINT_UNLOAD_FILAMENT: 
/* 361 */       return true;
/*     */     }
/*     */     
/* 364 */     return false;
/*     */   }
/*     */   
/*     */   public void startFilamentLoad() {
/* 368 */     this.xyz.startFilamentLoad();
/*     */   }
/*     */   
/*     */   public void stopFilamentLoad() {
/* 372 */     this.xyz.stopFilamentLoad();
/*     */   }
/*     */   
/*     */   public void startFilamentUnload() {
/* 376 */     this.xyz.startFilamentUnload();
/*     */   }
/*     */   
/*     */   public void stopFilamentUnload() {
/* 380 */     this.xyz.stopFilamentUnload();
/*     */   }
/*     */ }


/* Location:              D:\Misc\Downloads\XYZPrint\XYZMinimaker-1.0-SNAPSHOT.jar!\dk\fambagge\xyzminimaker\web\view\XyzBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */