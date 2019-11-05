package com.skybiz.wms02.m_DataObject;

public class Spacecraft_GRNR {
    int id;
    String N_o,RunNo,ItemCode,Description,
            AlternateItem,SupplierItemCode,BasCode,
            Remark,Qty;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getN_o() {
        return N_o;
    }

    public void setN_o(String n_o) {
        N_o = n_o;
    }

    public String getRunNo() {
        return RunNo;
    }

    public void setRunNo(String runNo) {
        RunNo = runNo;
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

    public String getAlternateItem() {
        return AlternateItem;
    }

    public void setAlternateItem(String alternateItem) {
        AlternateItem = alternateItem;
    }

    public String getSupplierItemCode() {
        return SupplierItemCode;
    }

    public void setSupplierItemCode(String supplierItemCode) {
        SupplierItemCode = supplierItemCode;
    }

    public String getBasCode() {
        return BasCode;
    }

    public void setBasCode(String basCode) {
        BasCode = basCode;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }
}
