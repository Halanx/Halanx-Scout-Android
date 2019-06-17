
package com.technicalrj.halanxscouts.Profile.ProfilePojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BankDetail {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("account_holder_name")
    @Expose
    private String accountHolderName;
    @SerializedName("account_number")
    @Expose
    private String accountNumber;
    @SerializedName("bank_name")
    @Expose
    private String bankName;
    @SerializedName("bank_branch")
    @Expose
    private String bankBranch;
    @SerializedName("ifsc_code")
    @Expose
    private String ifscCode;
    @SerializedName("scout")
    @Expose
    private Integer scout;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankBranch() {
        return bankBranch;
    }

    public void setBankBranch(String bankBranch) {
        this.bankBranch = bankBranch;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public Integer getScout() {
        return scout;
    }

    public void setScout(Integer scout) {
        this.scout = scout;
    }

    @Override
    public String toString() {
        return "BankDetail{" +
                "id=" + id +
                ", accountHolderName='" + accountHolderName + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", bankName='" + bankName + '\'' +
                ", bankBranch=" + bankBranch +
                ", ifscCode=" + ifscCode +
                ", scout=" + scout +
                '}';
    }
}
