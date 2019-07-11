package dk.fambagge.xyzminimaker.web.view;

import dk.fambagge.xyzminimaker.xyz.GcodeParser;
import dk.fambagge.xyzminimaker.xyz.XYZ;
import dk.fambagge.xyzminimaker.xyz.XYZInfo;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.component.UIComponent;
import javax.inject.Named;
import org.primefaces.component.tabview.Tab;
import org.primefaces.component.tabview.TabView;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;

@Named
@ApplicationScoped
public class XyzBean
        implements Serializable {

    private XYZ xyz;
    private long lastInfoCacheTimestamp;
    private XYZInfo cachedInfo;
    private GcodeParser.Gcode currentGcode;
    private double jogAmount;
    private int currentTab;

    @PostConstruct
    private void init() {
        System.out.println("Starting XYZ service!");
        this.xyz = new XYZ("COM3");
        this.lastInfoCacheTimestamp = -1L;
        this.currentGcode = null;
        this.jogAmount = 0.1D;
        this.currentTab = 0;
    }

    public XYZInfo getInfo() {
        if ((this.cachedInfo == null) || (System.currentTimeMillis() - this.lastInfoCacheTimestamp > 2000L)) {
            this.cachedInfo = this.xyz.queryAll();
            this.lastInfoCacheTimestamp = System.currentTimeMillis();
        }

        return this.cachedInfo;
    }

    public void handleUpload(FileUploadEvent event) {
        System.out.println("Upload: " + event.getFile().getFileName());
        try {
            this.currentGcode = GcodeParser.parse(event.getFile().getInputstream());

            System.out.println("Ready!");
        } catch (IOException ex) {
            Logger.getLogger(XyzBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void onTabChange(TabChangeEvent evt) {
        Tab targetTab = evt.getTab();
        TabView view = (TabView) targetTab.getParent();

        int clientActiveIndex = 0;

        for (UIComponent tab : view.getChildren()) {
            if (tab.equals(targetTab)) {
                break;
            }
            if (tab.isRendered()) {
                clientActiveIndex++;
            }
        }

        this.currentTab = clientActiveIndex;

        System.out.println("Active tab: " + this.currentTab);
    }

    public void print() {
        if (this.currentGcode != null) {
            this.xyz.printFile(this.currentGcode.getXyzEncoded());
        }
    }

    public void cancel() {
        if (isPrinting()) {
            this.xyz.cancel();
        }
    }

    public String getGcodeFormatted() {
        if (this.currentGcode != null) {
            return this.currentGcode.getGcode().replace("\n", ";N;");
        }
        return "";
    }

    public String getPrintTimeFormatted() {
        if (this.currentGcode != null) {
            int timeSeconds = this.currentGcode.getPrintTime();

            int timeMinutes = timeSeconds / 60;
            timeSeconds -= timeMinutes * 60;

            int timeHours = timeMinutes / 60;
            timeMinutes -= timeHours * 60;

            return pad(timeHours) + ":" + pad(timeMinutes) + ":" + pad(timeSeconds);
        }
        return "";
    }

    private String pad(int i) {
        String s = "" + i;
        if (s.length() == 1) {
            s = "0" + s;
        }

        return s;
    }

    public String getFilamentUseFormatted() {
        if (this.currentGcode != null) {
            return String.format("%.2f", this.currentGcode.getFilamentUsed() / 1000.0D);
        }
        return "";
    }

    public String getFilamentPriceFormatted() {
        if (this.currentGcode != null) {
            double usedFilamentM = this.currentGcode.getFilamentUsed() / 1000.0D;

            double pricePerM = 0.5D;

            return String.format("%.2f", usedFilamentM * pricePerM);
        }
        return "";
    }

    public boolean isIdle() {
        getInfo();

        return this.cachedInfo.getPrintStatus() == XYZInfo.PrintStatus.PRINT_NONE;
    }

    public boolean isPrinting() {
        getInfo();

        switch (this.cachedInfo.getPrintStatus()) {
            case PRINTING_IN_PROGRESS:
            case PRINT_HEATING:
            case PRINT_INITIAL:
                return true;
        }

        return false;
    }

    public boolean isPausable() {
        getInfo();

        switch (this.cachedInfo.getPrintStatus()) {
            case PRINTING_IN_PROGRESS:
                return true;
        }

        return false;
    }

    public boolean isPaused() {
        getInfo();

        return this.cachedInfo.getPrintStatus() == XYZInfo.PrintStatus.STATE_PRINT_PAUSE;
    }

    public boolean isJogMode() {
        getInfo();

        switch (this.cachedInfo.getPrintStatus()) {
            case PRINT_JOG_MODE:
            case STATE_PRINT_JOG_MODE:
                return true;
        }

        return false;
    }

    public boolean isShowPrintControls() {
        getInfo();

        switch (this.cachedInfo.getPrintStatus()) {
            case PRINTING_IN_PROGRESS:
            case PRINT_HEATING:
            case PRINT_INITIAL:
            case PRINT_NONE:
                return true;
        }

        return false;
    }

    public boolean isShowJogControls() {
        getInfo();

        switch (this.cachedInfo.getPrintStatus()) {
            case PRINT_JOG_MODE:
            case STATE_PRINT_JOG_MODE:
            case PRINT_NONE:
            case STATE_PRINT_HOMING:
                return true;
        }

        return false;
    }

    public boolean isShowFilamentControls() {
        getInfo();

        switch (this.cachedInfo.getPrintStatus()) {
            case PRINT_NONE:
            case STATE_PRINT_LOAD_FILAMENT:
            case STATE_PRINT_UNLOAD_FILAMENT:
                return true;
        }

        return false;
    }

    public boolean isShowCalibrateControls() {
        getInfo();

        switch (this.cachedInfo.getPrintStatus()) {
            case PRINT_NONE:
                return true;
        }

        return false;
    }

    public boolean isFilamentLoadPossible() {
        getInfo();

        switch (this.cachedInfo.getPrintStatus()) {
            case PRINT_NONE:
            case STATE_PRINT_LOAD_FILAMENT:
                return true;
        }

        return false;
    }

    public boolean isFilamentUnloadPossible() {
        getInfo();

        switch (this.cachedInfo.getPrintStatus()) {
            case PRINT_NONE:
            case STATE_PRINT_UNLOAD_FILAMENT:
                return true;
        }

        return false;
    }

    public GcodeParser.Gcode getCurrentGcode() {
        return this.currentGcode;
    }

    public double getJogAmount() {
        return this.jogAmount;
    }

    public void setJogAmount(double jogAmount) {
        this.jogAmount = jogAmount;
    }

    public int getCurrentTab() {
        return this.currentTab;
    }

    public void setCurrentTab(int currentTab) {
        this.currentTab = currentTab;
    }

    public void doHome() {
        this.xyz.home();
    }

    public void doJog(String axis, boolean negative) {
        int amount = (int) (this.jogAmount * 1000.0D);

        if ((!axis.equals("x")) && (!axis.equals("y")) && (!axis.equals("z"))) {
            System.out.println("Axis has to be one of x|y|z ...");
            return;
        }

        this.xyz.jog(amount, axis, negative);
    }

    public boolean isFilamentLoading() {
        getInfo();

        switch (this.cachedInfo.getPrintStatus()) {
            case STATE_PRINT_LOAD_FILAMENT:
                return true;
        }

        return false;
    }

    public boolean isFilamentUnloading() {
        getInfo();

        switch (this.cachedInfo.getPrintStatus()) {
            case STATE_PRINT_UNLOAD_FILAMENT:
                return true;
        }

        return false;
    }

    public void startFilamentLoad() {
        this.xyz.startFilamentLoad();
    }

    public void stopFilamentLoad() {
        this.xyz.stopFilamentLoad();
    }

    public void startFilamentUnload() {
        this.xyz.startFilamentUnload();
    }

    public void stopFilamentUnload() {
        this.xyz.stopFilamentUnload();
    }
}
