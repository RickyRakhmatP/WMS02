package com.skybiz.wms02.m_DataObject;

/**
 * Created by 7 on 25/04/2018.
 */

public class Spacecraft_Customer {
    int id;
    String CusCode,CusName,TermCode,D_ay,SalesPersonCode,Address;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCusCode() {
        return CusCode;
    }

    public void setCusCode(String cusCode) {
        CusCode = cusCode;
    }

    public String getCusName() {
        return CusName;
    }

    public void setCusName(String cusName) {
        CusName = cusName;
    }

    public String getTermCode() {
        return TermCode;
    }

    public void setTermCode(String termCode) {
        TermCode = termCode;
    }

    public String getD_ay() {
        return D_ay;
    }

    public void setD_ay(String d_ay) {
        D_ay = d_ay;
    }

    public String getSalesPersonCode() {
        return SalesPersonCode;
    }

    public void setSalesPersonCode(String salesPersonCode) {
        SalesPersonCode = salesPersonCode;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}
