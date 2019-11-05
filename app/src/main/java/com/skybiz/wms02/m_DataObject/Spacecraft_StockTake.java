package com.skybiz.wms02.m_DataObject;

public class Spacecraft_StockTake {
    int id;
    String AlternateItem,ItemCode,Description,
            UOM,ChkQty,LocationCode,
            D_ate,D_ateTime,RunNo,N_o;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAlternateItem() {
        return AlternateItem;
    }

    public void setAlternateItem(String alternateItem) {
        AlternateItem = alternateItem;
    }

    public String getItemCode() {
        return ItemCode;
    }

    public void setItemCode(String itemCode) {
        ItemCode = itemCode;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }

    public String getChkQty() {
        return ChkQty;
    }

    public void setChkQty(String chkQty) {
        ChkQty = chkQty;
    }

    public String getLocationCode() {
        return LocationCode;
    }

    public void setLocationCode(String locationCode) {
        LocationCode = locationCode;
    }

    public String getD_ate() {
        return D_ate;
    }

    public void setD_ate(String d_ate) {
        D_ate = d_ate;
    }

    public String getD_ateTime() {
        return D_ateTime;
    }

    public void setD_ateTime(String d_ateTime) {
        D_ateTime = d_ateTime;
    }

    public String getRunNo() {
        return RunNo;
    }

    public void setRunNo(String runNo) {
        RunNo = runNo;
    }

    public String getN_o() {
        return N_o;
    }

    public void setN_o(String n_o) {
        N_o = n_o;
    }
}
