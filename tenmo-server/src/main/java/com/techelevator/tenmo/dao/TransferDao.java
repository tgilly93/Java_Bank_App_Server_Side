package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {

    List<Transfer> getTransfers();

    Transfer getTransferByID(int id);
    List<Transfer> getTransfersByStatus(String status);
}
