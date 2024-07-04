package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transfer {
    private int transferID;
    private  int transferTypeID;
    private  int transferStatusID;
    private  int fromAccountID;
    private int toAccountID;
    private BigDecimal amount;

    public Transfer() {
    }

    public int getTransferID() {
        return transferID;
    }

    public void setTransferID(int transferID) {
        this.transferID = transferID;
    }

    public int getTransferTypeID() {
        return transferTypeID;
    }

    public void setTransferTypeID(int transferTypeID) {
        this.transferTypeID = transferTypeID;
    }

    public int getTransferStatusID() {
        return transferStatusID;
    }

    public void setTransferStatusID(int transferStatusID) {
        this.transferStatusID = transferStatusID;
    }

    public int getFromAccountID() {
        return fromAccountID;
    }

    public void setFromAccountID(int fromAccountID) {
        this.fromAccountID = fromAccountID;
    }

    public int getToAccountID() {
        return toAccountID;
    }

    public void setToAccountID(int toAccountID) {
        this.toAccountID = toAccountID;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
