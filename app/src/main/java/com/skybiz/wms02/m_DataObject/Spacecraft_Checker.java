package com.skybiz.wms02.m_DataObject;

public class Spacecraft_Checker {
    int id;
    String CheckerCode,CheckerName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCheckerCode() {
        return CheckerCode;
    }

    public void setCheckerCode(String checkerCode) {
        CheckerCode = checkerCode;
    }

    public String getCheckerName() {
        return CheckerName;
    }

    public void setCheckerName(String checkerName) {
        CheckerName = checkerName;
    }
}
