/*     */ package dk.fambagge.xyzminimaker.xyz;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class XYZInfo
/*     */ {
/*     */   public String getPrinterName()
/*     */   {
/*  20 */     return this.printerName;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getModelNumber()
/*     */   {
/*  27 */     return this.modelNumber;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getPrinterSerial()
/*     */   {
/*  34 */     return this.printerSerial;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getFirmwareVersion()
/*     */   {
/*  41 */     return this.firmwareVersion;
/*     */   }
/*     */   
/*  44 */   private int[] calibration = new int[0];
/*     */   private int percentComplete;
/*     */   private int elapsedTimeMinutes;
/*     */   private int estimatedTimeLeftMinutes;
/*     */   private int errorCode;
/*  49 */   private String filamentSerial = "";
/*     */   private int spoolLeftMM;
/*  51 */   private PrintStatus printStatus = PrintStatus.UNKNOWN;
/*     */   private int printSubStatus;
/*     */   private int packageSize;
/*     */   private boolean autoLeveling;
/*     */   private int extruderTemp;
/*     */   private int extruderTargetTemp;
/*     */   private int bedTemp;
/*     */   private int zOffset;
/*  59 */   private String printerName = "Unknown";
/*  60 */   private String modelNumber = "";
/*  61 */   private String printerSerial = "";
/*  62 */   private String firmwareVersion = "0.0.0";
/*     */   
/*     */ 
/*     */   public void parse(String paramString)
/*     */   {
/*     */     String[] arrayOfString;
/*     */     
/*  69 */     if (paramString.startsWith("t:")) {
/*  70 */       arrayOfString = paramString.substring(2).split(",");
/*     */       
/*  72 */       this.extruderTemp = Integer.parseInt(arrayOfString[1]);
/*  73 */       this.extruderTargetTemp = Integer.parseInt(arrayOfString[2]);
/*  74 */     } else if (paramString.startsWith("f:")) {
/*  75 */       arrayOfString = paramString.substring(2).split(",");
/*     */       
/*  77 */       this.spoolLeftMM = Integer.parseInt(arrayOfString[1]);
/*  78 */     } else if (paramString.startsWith("w:")) {
/*  79 */       arrayOfString = paramString.substring(2).split(",");
/*     */       
/*  81 */       this.filamentSerial = arrayOfString[1];
/*  82 */     } else if (paramString.startsWith("o:")) {
/*  83 */       arrayOfString = paramString.substring(2).split(",");
/*     */       
/*  85 */       this.packageSize = (Integer.parseInt(arrayOfString[0].substring(1)) * 1024);
/*  86 */       this.autoLeveling = arrayOfString[3].equals("a+");
/*  87 */     } else if (paramString.startsWith("j:")) {
/*  88 */       arrayOfString = paramString.substring(2).split(",");
/*     */       
/*  90 */       this.printStatus = PrintStatus.fromInt(Integer.parseInt(arrayOfString[0]));
/*  91 */       this.printSubStatus = Integer.parseInt(arrayOfString[1]);
/*  92 */     } else if (paramString.startsWith("b:")) {
/*  93 */       this.bedTemp = Integer.parseInt(paramString.substring(2));
/*  94 */     } else if (paramString.startsWith("e:")) {
/*  95 */       this.errorCode = Integer.parseInt(paramString.substring(2));
/*  96 */     } else if (paramString.startsWith("z:")) {
/*  97 */       this.zOffset = Integer.parseInt(paramString.substring(2));
/*  98 */     } else if (paramString.startsWith("n:")) {
/*  99 */       this.printerName = paramString.substring(2);
/* 100 */     } else if (paramString.startsWith("v:")) {
/* 101 */       this.firmwareVersion = paramString.substring(2);
/* 102 */     } else if (paramString.startsWith("p:")) {
/* 103 */       this.modelNumber = paramString.substring(2);
/* 104 */     } else if (paramString.startsWith("i:")) {
/* 105 */       this.printerSerial = paramString.substring(2);
/* 106 */     } else if (paramString.startsWith("c:")) {
/* 107 */       arrayOfString = paramString.substring(3, paramString.length() - 1).split(",");
/*     */       
/* 109 */       this.calibration = new int[arrayOfString.length];
/* 110 */       for (int i = 0; i < arrayOfString.length; i++) {
/* 111 */         this.calibration[i] = Integer.parseInt(arrayOfString[i]);
/*     */       }
/* 113 */     } else if (paramString.startsWith("d:")) {
/* 114 */       arrayOfString = paramString.substring(2).split(",");
/*     */       
/* 116 */       this.percentComplete = Integer.parseInt(arrayOfString[0]);
/* 117 */       this.elapsedTimeMinutes = Integer.parseInt(arrayOfString[1]);
/* 118 */       this.estimatedTimeLeftMinutes = Integer.parseInt(arrayOfString[2]);
/*     */     } else {
/* 120 */       System.out.println("Unhandled line [" + paramString + "]");
/*     */     }
/*     */   }
/*     */   
/*     */   public String toString() {
/* 125 */     StringBuilder localStringBuilder = new StringBuilder();
/*     */     
/* 127 */     localStringBuilder.append("XYZInfo:\n");
/* 128 */     localStringBuilder.append("\tPercent complete: ").append(this.percentComplete).append("\n");
/* 129 */     localStringBuilder.append("\tElapsed time: ").append(this.elapsedTimeMinutes).append(" minutes\n");
/* 130 */     localStringBuilder.append("\tEstimated time left: ").append(this.estimatedTimeLeftMinutes).append(" minutes\n");
/* 131 */     localStringBuilder.append("\tCalibration: ").append(Arrays.toString(this.calibration)).append("\n");
/* 132 */     localStringBuilder.append("\tExtruder target temp: ").append(getExtruderTargetTemp()).append("\n");
/* 133 */     localStringBuilder.append("\tExtruder temp: ").append(getExtruderTemp()).append("\n");
/* 134 */     localStringBuilder.append("\tSpool left: ").append(this.spoolLeftMM).append(" mm\n");
/* 135 */     localStringBuilder.append("\tPrinter name: ").append(getPrinterName()).append("\n");
/* 136 */     localStringBuilder.append("\tModel: ").append(getModelNumber()).append("\n");
/* 137 */     localStringBuilder.append("\tSerial: ").append(getPrinterSerial()).append("\n");
/* 138 */     localStringBuilder.append("\tPackage size: ").append(this.packageSize).append("\n");
/* 139 */     localStringBuilder.append("\tAuto leveling: ").append(this.autoLeveling).append("\n");
/* 140 */     localStringBuilder.append("\tPrinter status: ").append(this.printStatus).append("\n");
/* 141 */     localStringBuilder.append("\tPrinter sub status: ").append(this.printSubStatus).append("\n");
/* 142 */     localStringBuilder.append("\tFilament serial: ").append(this.filamentSerial).append("\n");
/* 143 */     localStringBuilder.append("\tFilament name: ").append(getFilamentName()).append("\n");
/*     */     
/* 145 */     return localStringBuilder.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int[] getCalibration()
/*     */   {
/* 152 */     return this.calibration;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getPercentComplete()
/*     */   {
/* 159 */     return this.percentComplete;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getElapsedTimeMinutes()
/*     */   {
/* 166 */     return this.elapsedTimeMinutes;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getEstimatedTimeLeftMinutes()
/*     */   {
/* 173 */     return this.estimatedTimeLeftMinutes;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getErrorCode()
/*     */   {
/* 180 */     return this.errorCode;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getFilamentSerial()
/*     */   {
/* 187 */     return this.filamentSerial;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getSpoolLeftMM()
/*     */   {
/* 194 */     return this.spoolLeftMM;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public PrintStatus getPrintStatus()
/*     */   {
/* 201 */     return this.printStatus;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getPrintSubStatus()
/*     */   {
/* 208 */     return this.printSubStatus;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getPackageSize()
/*     */   {
/* 215 */     return this.packageSize;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isAutoLeveling()
/*     */   {
/* 222 */     return this.autoLeveling;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getExtruderTemp()
/*     */   {
/* 229 */     return this.extruderTemp;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getExtruderTargetTemp()
/*     */   {
/* 236 */     return this.extruderTargetTemp;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getBedTemp()
/*     */   {
/* 243 */     return this.bedTemp;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getzOffset()
/*     */   {
/* 250 */     return this.zOffset;
/*     */   }
/*     */   
/*     */   public double getFilamentTotalLength() {
/* 254 */     if (this.filamentSerial.length() < 4) {
/* 255 */       return -1.0D;
/*     */     }
/*     */     
/* 258 */     switch (this.filamentSerial.charAt(3)) {
/*     */     case '3': 
/* 260 */       return 100.0D;
/*     */     case '5': 
/* 262 */       return 200.0D;
/*     */     case '6': 
/* 264 */       return 300.0D;
/*     */     }
/*     */     
/* 267 */     return -1.0D;
/*     */   }
/*     */   
/*     */   public String getFilamentName() {
/* 271 */     if (this.filamentSerial.length() < 5) {
/* 272 */       return "";
/*     */     }
/*     */     
/* 275 */     switch (this.filamentSerial.charAt(4)) {
/*     */     case '0': 
/* 277 */       return "Bronze";
/*     */     case '1': 
/* 279 */       return "Silver";
/*     */     case '2': 
/* 281 */       return "Clear Red";
/*     */     case '3': 
/* 283 */       return "Clear";
/*     */     case '4': 
/* 285 */       return "Bottle Green";
/*     */     case '5': 
/* 287 */       return "Neon Magenta";
/*     */     case '6': 
/* 289 */       return "SteelBlue";
/*     */     case '7': 
/* 291 */       return "Sun Orange";
/*     */     case '8': 
/* 293 */       return "Pearl White";
/*     */     case '9': 
/* 295 */       return "Copper";
/*     */     case 'A': 
/* 297 */       return "Purple";
/*     */     case 'B': 
/* 299 */       return "Blue";
/*     */     case 'C': 
/* 301 */       return "Neon Tangerine";
/*     */     case 'D': 
/* 303 */       return "Viridity";
/*     */     case 'E': 
/* 305 */       return "Olivine";
/*     */     case 'F': 
/* 307 */       return "Gold";
/*     */     case 'G': 
/* 309 */       return "Green";
/*     */     case 'H': 
/* 311 */       return "Neon Green";
/*     */     case 'I': 
/* 313 */       return "Snow White";
/*     */     case 'J': 
/* 315 */       return "Neon Yellow";
/*     */     case 'K': 
/* 317 */       return "Black";
/*     */     case 'L': 
/* 319 */       return "Violet";
/*     */     case 'M': 
/* 321 */       return "Grape Purple";
/*     */     case 'N': 
/* 323 */       return "Purpurine";
/*     */     case 'O': 
/* 325 */       return "Clear Yellow";
/*     */     case 'P': 
/* 327 */       return "Clear Green";
/*     */     case 'Q': 
/* 329 */       return "Clear Tangerine";
/*     */     case 'R': 
/* 331 */       return "Red";
/*     */     case 'S': 
/* 333 */       return "Cyber Yellow";
/*     */     case 'T': 
/* 335 */       return "Tangerine";
/*     */     case 'U': 
/* 337 */       return "Clear Blue";
/*     */     case 'V': 
/* 339 */       return "Clear Purple";
/*     */     case 'W': 
/* 341 */       return "White";
/*     */     case 'X': 
/* 343 */       return "Clear Magenta";
/*     */     case 'Y': 
/* 345 */       return "Yellow";
/*     */     case 'Z': 
/* 347 */       return "Nature";
/*     */     }
/* 349 */     return "--";
/*     */   }
/*     */   
/*     */   public static enum PrintStatus
/*     */   {
/* 354 */     UNKNOWN(-1), 
/* 355 */     PRINT_INITIAL(9500), 
/* 356 */     PRINT_HEATING(9501), 
/* 357 */     PRINTING(9502), 
/* 358 */     PRINT_CALIBRATING(9503), 
/* 359 */     PRINT_CALIBRATING_DONE(9504), 
/* 360 */     PRINTING_IN_PROGRESS(9505), 
/* 361 */     PRINT_COOLING_DONE(9506), 
/* 362 */     PRINT_COOLING_END(9507), 
/* 363 */     PRINT_ENDING_PROCESS(9508), 
/* 364 */     PRINT_ENDING_PROCESS_DONE(9509), 
/* 365 */     PRINT_JOB_DONE(9510), 
/* 366 */     PRINT_NONE(9511), 
/* 367 */     PRINT_STOP(9512), 
/* 368 */     PRINT_LOAD_FILAMENT(9513), 
/* 369 */     PRINT_UNLOAD_FILAMENT(9514), 
/* 370 */     PRINT_AUTO_CALIBRATION(9515), 
/* 371 */     PRINT_JOG_MODE(9516), 
/* 372 */     PRINT_FATAL_ERROR(9517), 
/* 373 */     STATE_PRINT_LOAD_FILAMENT(9530), 
/* 374 */     STATE_PRINT_UNLOAD_FILAMENT(9531), 
/* 375 */     STATE_PRINT_JOG_MODE(9532), 
/* 376 */     STATE_PRINT_FATAL_ERROR(9533), 
/* 377 */     STATE_PRINT_HOMING(9534), 
/* 378 */     STATE_PRINT_CALIBRATE(9535), 
/* 379 */     STATE_PRINT_ADJUST_ZOFFSET(9540), 
/* 380 */     STATE_PRINT_PAUSE(9601), 
/* 381 */     STATE_PRINT_CANCELLING(9602), 
/* 382 */     STATE_PRINT_BUSY(9700);
/*     */     
/*     */     private static PrintStatus fromInt(int paramInt) {
/* 385 */       for (PrintStatus localPrintStatus : PrintStatus.values()) {
/* 386 */         if (localPrintStatus.status == paramInt) {
/* 387 */           return localPrintStatus;
/*     */         }
/*     */       }
/*     */       
/* 391 */       return UNKNOWN;
/*     */     }
/*     */     
/*     */     private int status;
/*     */     private PrintStatus(int paramInt) {
/* 396 */       this.status = paramInt;
/*     */     }
/*     */   }
/*     */   
/*     */   public String getSpoolLeftMFormatted() {
/* 401 */     return String.format("%.2f", new Object[] { Double.valueOf(this.spoolLeftMM / 1000.0D) }).replace(",", ".");
/*     */   }
/*     */ }


/* Location:              D:\Misc\Downloads\XYZPrint\XYZMinimaker-1.0-SNAPSHOT.jar!\dk\fambagge\xyzminimaker\xyz\XYZInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */