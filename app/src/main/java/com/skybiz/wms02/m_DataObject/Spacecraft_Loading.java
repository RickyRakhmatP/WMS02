package com.skybiz.wms02.m_DataObject;

public class Spacecraft_Loading {
    int id;
    String RunNo,Bay,CheckerCode,DriverCode,LorryNo,D_ateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRunNo() {
        return RunNo;
    }

    public void setRunNo(String runNo) {
        RunNo = runNo;
    }

    public String getBay() {
        return Bay;
    }

    public void setBay(String bay) {
        Bay = bay;
    }

    public String getCheckerCode() {
        return CheckerCode;
    }

    public void setCheckerCode(String checkerCode) {
        CheckerCode = checkerCode;
    }

    public String getDriverCode() {
        return DriverCode;
    }

    public void setDriverCode(String driverCode) {
        DriverCode = driverCode;
    }

    public String getLorryNo() {
        return LorryNo;
    }

    public void setLorryNo(String lorryNo) {
        LorryNo = lorryNo;
    }

    public String getD_ateTime() {
        return D_ateTime;
    }

    public void setD_ateTime(String d_ateTime) {
        D_ateTime = d_ateTime;
    }
}
