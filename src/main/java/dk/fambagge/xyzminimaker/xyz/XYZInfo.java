package dk.fambagge.xyzminimaker.xyz;

import java.util.Arrays;

public class XYZInfo {

    public String getPrinterName() {
        return this.printerName;
    }

    public String getModelNumber() {
        return this.modelNumber;
    }

    public String getPrinterSerial() {
        return this.printerSerial;
    }

    public String getFirmwareVersion() {
        return this.firmwareVersion;
    }

    private int[] calibration = new int[0];
    private int percentComplete;
    private int elapsedTimeMinutes;
    private int estimatedTimeLeftMinutes;
    private int errorCode;
    private String filamentSerial = "";
    private int spoolLeftMM;
    private PrintStatus printStatus = PrintStatus.UNKNOWN;
    private int printSubStatus;
    private int packageSize;
    private boolean autoLeveling;
    private int extruderTemp;
    private int extruderTargetTemp;
    private int bedTemp;
    private int zOffset;
    private String printerName = "Unknown";
    private String modelNumber = "";
    private String printerSerial = "";
    private String firmwareVersion = "0.0.0";

    public void parse(String line) {
        if (line.startsWith("t:")) {
            String[] split = line.substring(2).split(",");

            this.extruderTemp = Integer.parseInt(split[1]);
            this.extruderTargetTemp = Integer.parseInt(split[2]);
        } else if (line.startsWith("f:")) {
            String[] split = line.substring(2).split(",");

            this.spoolLeftMM = Integer.parseInt(split[1]);
        } else if (line.startsWith("w:")) {
            String[] split = line.substring(2).split(",");

            this.filamentSerial = split[1];
        } else if (line.startsWith("o:")) {
            String[] split = line.substring(2).split(",");

            this.packageSize = (Integer.parseInt(split[0].substring(1)) * 1024);
            this.autoLeveling = split[3].equals("a+");
        } else if (line.startsWith("j:")) {
            String[] split = line.substring(2).split(",");

            this.printStatus = PrintStatus.fromInt(Integer.parseInt(split[0]));
            this.printSubStatus = Integer.parseInt(split[1]);
        } else if (line.startsWith("b:")) {
            this.bedTemp = Integer.parseInt(line.substring(2));
        } else if (line.startsWith("e:")) {
            this.errorCode = Integer.parseInt(line.substring(2));
        } else if (line.startsWith("z:")) {
            this.zOffset = Integer.parseInt(line.substring(2));
        } else if (line.startsWith("n:")) {
            this.printerName = line.substring(2);
        } else if (line.startsWith("v:")) {
            this.firmwareVersion = line.substring(2);
        } else if (line.startsWith("p:")) {
            this.modelNumber = line.substring(2);
        } else if (line.startsWith("i:")) {
            this.printerSerial = line.substring(2);
        } else if (line.startsWith("c:")) {
            String[] split = line.substring(3, line.length() - 1).split(",");

            this.calibration = new int[split.length];
            for (int i = 0; i < split.length; i++) {
                this.calibration[i] = Integer.parseInt(split[i]);
            }
        } else if (line.startsWith("d:")) {
            String[] split = line.substring(2).split(",");

            this.percentComplete = Integer.parseInt(split[0]);
            this.elapsedTimeMinutes = Integer.parseInt(split[1]);
            this.estimatedTimeLeftMinutes = Integer.parseInt(split[2]);
        } else {
            System.out.println("Unhandled line [" + line + "]");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("XYZInfo:\n");
        sb.append("\tPercent complete: ").append(this.percentComplete).append("\n");
        sb.append("\tElapsed time: ").append(this.elapsedTimeMinutes).append(" minutes\n");
        sb.append("\tEstimated time left: ").append(this.estimatedTimeLeftMinutes).append(" minutes\n");
        sb.append("\tCalibration: ").append(Arrays.toString(this.calibration)).append("\n");
        sb.append("\tExtruder target temp: ").append(getExtruderTargetTemp()).append("\n");
        sb.append("\tExtruder temp: ").append(getExtruderTemp()).append("\n");
        sb.append("\tSpool left: ").append(this.spoolLeftMM).append(" mm\n");
        sb.append("\tPrinter name: ").append(getPrinterName()).append("\n");
        sb.append("\tModel: ").append(getModelNumber()).append("\n");
        sb.append("\tSerial: ").append(getPrinterSerial()).append("\n");
        sb.append("\tPackage size: ").append(this.packageSize).append("\n");
        sb.append("\tAuto leveling: ").append(this.autoLeveling).append("\n");
        sb.append("\tPrinter status: ").append(this.printStatus).append("\n");
        sb.append("\tPrinter sub status: ").append(this.printSubStatus).append("\n");
        sb.append("\tFilament serial: ").append(this.filamentSerial).append("\n");
        sb.append("\tFilament name: ").append(getFilamentName()).append("\n");

        return sb.toString();
    }

    public int[] getCalibration() {
        return this.calibration;
    }

    public int getPercentComplete() {
        return this.percentComplete;
    }

    public int getElapsedTimeMinutes() {
        return this.elapsedTimeMinutes;
    }

    public int getEstimatedTimeLeftMinutes() {
        return this.estimatedTimeLeftMinutes;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public String getFilamentSerial() {
        return this.filamentSerial;
    }

    public int getSpoolLeftMM() {
        return this.spoolLeftMM;
    }

    public PrintStatus getPrintStatus() {
        return this.printStatus;
    }

    public int getPrintSubStatus() {
        return this.printSubStatus;
    }

    public int getPackageSize() {
        return this.packageSize;
    }

    public boolean isAutoLeveling() {
        return this.autoLeveling;
    }

    public int getExtruderTemp() {
        return this.extruderTemp;
    }

    public int getExtruderTargetTemp() {
        return this.extruderTargetTemp;
    }

    public int getBedTemp() {
        return this.bedTemp;
    }

    public int getzOffset() {
        return this.zOffset;
    }

    public double getFilamentTotalLength() {
        if (this.filamentSerial.length() < 4) {
            return -1.0D;
        }

        switch (this.filamentSerial.charAt(3)) {
            case '3':
                return 100.0D;
            case '5':
                return 200.0D;
            case '6':
                return 300.0D;
        }

        return -1.0D;
    }

    public String getFilamentName() {
        if (this.filamentSerial.length() < 5) {
            return "";
        }

        switch (this.filamentSerial.charAt(4)) {
            case '0':
                return "Bronze";
            case '1':
                return "Silver";
            case '2':
                return "Clear Red";
            case '3':
                return "Clear";
            case '4':
                return "Bottle Green";
            case '5':
                return "Neon Magenta";
            case '6':
                return "SteelBlue";
            case '7':
                return "Sun Orange";
            case '8':
                return "Pearl White";
            case '9':
                return "Copper";
            case 'A':
                return "Purple";
            case 'B':
                return "Blue";
            case 'C':
                return "Neon Tangerine";
            case 'D':
                return "Viridity";
            case 'E':
                return "Olivine";
            case 'F':
                return "Gold";
            case 'G':
                return "Green";
            case 'H':
                return "Neon Green";
            case 'I':
                return "Snow White";
            case 'J':
                return "Neon Yellow";
            case 'K':
                return "Black";
            case 'L':
                return "Violet";
            case 'M':
                return "Grape Purple";
            case 'N':
                return "Purpurine";
            case 'O':
                return "Clear Yellow";
            case 'P':
                return "Clear Green";
            case 'Q':
                return "Clear Tangerine";
            case 'R':
                return "Red";
            case 'S':
                return "Cyber Yellow";
            case 'T':
                return "Tangerine";
            case 'U':
                return "Clear Blue";
            case 'V':
                return "Clear Purple";
            case 'W':
                return "White";
            case 'X':
                return "Clear Magenta";
            case 'Y':
                return "Yellow";
            case 'Z':
                return "Nature";
        }
        return "--";
    }

    public static enum PrintStatus {
        UNKNOWN(-1),
        PRINT_INITIAL(9500),
        PRINT_HEATING(9501),
        PRINTING(9502),
        PRINT_CALIBRATING(9503),
        PRINT_CALIBRATING_DONE(9504),
        PRINTING_IN_PROGRESS(9505),
        PRINT_COOLING_DONE(9506),
        PRINT_COOLING_END(9507),
        PRINT_ENDING_PROCESS(9508),
        PRINT_ENDING_PROCESS_DONE(9509),
        PRINT_JOB_DONE(9510),
        PRINT_NONE(9511),
        PRINT_STOP(9512),
        PRINT_LOAD_FILAMENT(9513),
        PRINT_UNLOAD_FILAMENT(9514),
        PRINT_AUTO_CALIBRATION(9515),
        PRINT_JOG_MODE(9516),
        PRINT_FATAL_ERROR(9517),
        STATE_PRINT_LOAD_FILAMENT(9530),
        STATE_PRINT_UNLOAD_FILAMENT(9531),
        STATE_PRINT_JOG_MODE(9532),
        STATE_PRINT_FATAL_ERROR(9533),
        STATE_PRINT_HOMING(9534),
        STATE_PRINT_CALIBRATE(9535),
        STATE_PRINT_ADJUST_ZOFFSET(9540),
        STATE_PRINT_PAUSE(9601),
        STATE_PRINT_CANCELLING(9602),
        STATE_PRINT_BUSY(9700);

        private static PrintStatus fromInt(int status) {
            for (PrintStatus p : PrintStatus.values()) {
                if (p.status == status) {
                    return p;
                }
            }

            return UNKNOWN;
        }

        private final int status;

        private PrintStatus(int status) {
            this.status = status;
        }
    }

    public String getSpoolLeftMFormatted() {
        return String.format("%.2f", this.spoolLeftMM / 1000.0D).replace(",", ".");
    }
}
