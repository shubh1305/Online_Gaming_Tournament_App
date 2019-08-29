package com.example.pubgbattle;

public class TransactionDetail {
    public String transactionId, transactionType, transactionTitle, transactionDate, transactionTime;
    int transactionAmount;
    long transactionDateInt;

    public TransactionDetail(){}

    public TransactionDetail(String transactionId, String transactionType,
                             String transactionTitle, String transactionDate, String transactionTime, int transactionAmount,
                             long transactionDateInt) {
        this.transactionId = transactionId;
        this.transactionType = transactionType;
        this.transactionTitle = transactionTitle;
        this.transactionDate = transactionDate;
        this.transactionTime = transactionTime;
        this.transactionAmount = transactionAmount;
        this.transactionDateInt = transactionDateInt;
    }

    public long getTransactionDateInt() {
        return transactionDateInt;
    }

    public void setTransactionDateInt(long transactionDateInt) {
        this.transactionDateInt = transactionDateInt;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionTitle() {
        return transactionTitle;
    }

    public void setTransactionTitle(String transactionTitle) {
        this.transactionTitle = transactionTitle;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    public int getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(int transactionAmount) {
        this.transactionAmount = transactionAmount;
    }
}
