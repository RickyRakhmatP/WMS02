package com.skybiz.wms02.m_DataObject;

public class Spacecraft_Dt {
    int id;
    String RunNo,Doc1No, ItemCode, Description,
            Qty, FactorQty, UOM,
            UOMSingular, HCUnitCost, DisRate1,
            HCDiscount, TaxRate1, DetailTaxCode,
            HCTax, HCLineAmt, DepartmentCode,
            BranchCode, ProjectCode, LocationCode,
            ItemBatch, WarrantyDate;

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

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }

    public String getFactorQty() {
        return FactorQty;
    }

    public void setFactorQty(String factorQty) {
        FactorQty = factorQty;
    }

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }

    public String getUOMSingular() {
        return UOMSingular;
    }

    public void setUOMSingular(String UOMSingular) {
        this.UOMSingular = UOMSingular;
    }

    public String getHCUnitCost() {
        return HCUnitCost;
    }

    public void setHCUnitCost(String HCUnitCost) {
        this.HCUnitCost = HCUnitCost;
    }

    public String getDisRate1() {
        return DisRate1;
    }

    public void setDisRate1(String disRate1) {
        DisRate1 = disRate1;
    }

    public String getHCDiscount() {
        return HCDiscount;
    }

    public void setHCDiscount(String HCDiscount) {
        this.HCDiscount = HCDiscount;
    }

    public String getTaxRate1() {
        return TaxRate1;
    }

    public void setTaxRate1(String taxRate1) {
        TaxRate1 = taxRate1;
    }

    public String getDetailTaxCode() {
        return DetailTaxCode;
    }

    public void setDetailTaxCode(String detailTaxCode) {
        DetailTaxCode = detailTaxCode;
    }

    public String getHCTax() {
        return HCTax;
    }

    public void setHCTax(String HCTax) {
        this.HCTax = HCTax;
    }

    public String getHCLineAmt() {
        return HCLineAmt;
    }

    public void setHCLineAmt(String HCLineAmt) {
        this.HCLineAmt = HCLineAmt;
    }

    public String getDepartmentCode() {
        return DepartmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        DepartmentCode = departmentCode;
    }

    public String getBranchCode() {
        return BranchCode;
    }

    public void setBranchCode(String branchCode) {
        BranchCode = branchCode;
    }

    public String getProjectCode() {
        return ProjectCode;
    }

    public void setProjectCode(String projectCode) {
        ProjectCode = projectCode;
    }

    public String getLocationCode() {
        return LocationCode;
    }

    public void setLocationCode(String locationCode) {
        LocationCode = locationCode;
    }

    public String getItemBatch() {
        return ItemBatch;
    }

    public void setItemBatch(String itemBatch) {
        ItemBatch = itemBatch;
    }

    public String getWarrantyDate() {
        return WarrantyDate;
    }

    public void setWarrantyDate(String warrantyDate) {
        WarrantyDate = warrantyDate;
    }
}
