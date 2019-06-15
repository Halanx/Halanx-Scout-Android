package com.technicalrj.halanxscouts.Wallet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TaskPayment {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("paid_on")
    @Expose
    private String paidOn;
    @SerializedName("amount")
    @Expose
    private Integer amount;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("wallet")
    @Expose
    private Integer wallet;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPaidOn() {
        return paidOn;
    }

    public void setPaidOn(String paidOn) {
        this.paidOn = paidOn;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getWallet() {
        return wallet;
    }

    public void setWallet(Integer wallet) {
        this.wallet = wallet;
    }

}