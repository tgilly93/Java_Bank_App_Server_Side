package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {

    List<Transfer> getTransfers(int userID);
    Transfer sendTransfer(int sendToId, int userId, BigDecimal amount);

    Transfer requestTransfer(int requestFromID, int userId, BigDecimal amount);

    Transfer getTransferByID(int id);
    List<Transfer> getTransfersByStatus(String status, int userID);
}
