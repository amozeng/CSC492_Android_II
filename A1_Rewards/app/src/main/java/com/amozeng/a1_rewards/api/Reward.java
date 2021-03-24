package com.amozeng.a1_rewards.api;

import java.io.Serializable;

public class Reward implements Serializable {

    private String receiverUser;
    private String giverName;
    private String giverUser;
    private String amount;
    private String note;
    private String awardDate;

    public Reward() {
    }

    public String getGiverName() { return this.giverName; }
    public String getAmount() { return this.amount; }
    public String getNote() { return this.note; }
    public String getAwardDate() { return this.awardDate; }
    public String getReceiverUser() { return this.receiverUser; }
    public String getGiverUser() { return this.giverUser; }

    public void setReceiverUser(String receiverUser) { this.receiverUser = receiverUser; }
    public void setGiverName(String giverName) {this.giverName = giverName; }
    public void setGiverUser(String giverUser) { this.giverUser = giverUser; }
    public void setAmount(String amount) { this.amount = amount; }
    public void setNote(String note) {this.note = note; }
    public void setAwardDate(String awardDate) { this.awardDate = awardDate; }
}
