package com.skybiz.wms02.m_DataObject;

/**
 * Created by 7 on 03/11/2017.
 */

public class Spacecraft_Detail {
    String BtnQty, CurCode, Doc1No,ItemCode,
            Description, ItemGroup, HCUnitCost,HCDiscount,
            DisRate1,PhotoFile,Description2, UOM,
            FactorQty,BlankLine,DetailTaxCode;
    int RunNo;

    public String getBtnQty() {
        return BtnQty;
    }

    public void setBtnQty(String btnQty) {
        BtnQty = btnQty;
    }

    public String getCurCode() {
        return CurCode;
    }

    public void setCurCode(String curCode) {
        CurCode = curCode;
    }

    public String getDoc1No() {
        return Doc1No;
    }

    public void setDoc1No(String doc1No) {
        Doc1No = doc1No;
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

    public String getItemGroup() {
        return ItemGroup;
    }

    public void setItemGroup(String itemGroup) {
        ItemGroup = itemGroup;
    }

    public String getHCUnitCost() {
        return HCUnitCost;
    }

    public void setHCUnitCost(String HCUnitCost) {
        this.HCUnitCost = HCUnitCost;
    }

    public String getHCDiscount() {
        return HCDiscount;
    }

    public void setHCDiscount(String HCDiscount) {
        this.HCDiscount = HCDiscount;
    }

    public String getDisRate1() {
        return DisRate1;
    }

    public void setDisRate1(String disRate1) {
        DisRate1 = disRate1;
    }

    public String getPhotoFile() {
        return PhotoFile;
    }

    public void setPhotoFile(String photoFile) {
        PhotoFile = photoFile;
    }

    public String getDescription2() {
        return Description2;
    }

    public void setDescription2(String description2) {
        Description2 = description2;
    }

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }

    public String getFactorQty() {
        return FactorQty;
    }

    public void setFactorQty(String factorQty) {
        FactorQty = factorQty;
    }

    public String getBlankLine() {
        return BlankLine;
    }

    public void setBlankLine(String blankLine) {
        BlankLine = blankLine;
    }

    public String getDetailTaxCode() {
        return DetailTaxCode;
    }

    public void setDetailTaxCode(String detailTaxCode) {
        DetailTaxCode = detailTaxCode;
    }

    public int getRunNo() {
        return RunNo;
    }

    public void setRunNo(int runNo) {
        RunNo = runNo;
    }
}
