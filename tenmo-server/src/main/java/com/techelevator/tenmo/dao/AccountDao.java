package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;

public interface AccountDao {
    Account getAccountByUserID(int id);
    void updateBalance(Transfer transfer);
}
