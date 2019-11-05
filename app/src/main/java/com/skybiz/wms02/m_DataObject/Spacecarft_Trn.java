package com.skybiz.wms02.m_DataObject;

public class Spacecarft_Trn {
    int id;
    String Doc1No,D_ate,CusName,TotalQty,HCNetAmt,SyncYN;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDoc1No() {
        return Doc1No;
    }

    public void setDoc1No(String doc1No) {
        Doc1No = doc1No;
    }

    public String getD_ate() {
        return D_ate;
    }

    public void setD_ate(String d_ate) {
        D_ate = d_ate;
    }

    public String getCusName() {
        return CusName;
    }

    public void setCusName(String cusName) {
        CusName = cusName;
    }

    public String getTotalQty() {
        return TotalQty;
    }

    public void setTotalQty(String totalQty) {
        TotalQty = totalQty;
    }

    public String getHCNetAmt() {
        return HCNetAmt;
    }

    public void setHCNetAmt(String HCNetAmt) {
        this.HCNetAmt = HCNetAmt;
    }

    public String getSyncYN() {
        return SyncYN;
    }

    public void setSyncYN(String syncYN) {
        SyncYN = syncYN;
    }
}
