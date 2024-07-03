package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.List;

public class JdbcTransferDao implements TransferDao, AccountDao{
    private final JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Account getAccountByID(int id) {
       Account account = null;
       String sql = "SELECT account_id, user_id, balance\n" +
               "\tFROM public.account\n" +
               "\tWhere user_id = ?;";
       try{
           SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);
           if(result.next()){
               account = mapToAccount(result);
           }
       }
    }

    private Account mapToAccount(SqlRowSet result) {
        Account account = new Account();
        account.setUserID(result.getInt("user_id"));
        account.setBalance(result.getBigDecimal("balance"));


        return  account;
    }

    @Override
    public List<Transfer> getTransfers() {
        return null;
    }
    @Override
    public Transfer getTransferByID(int id) {
        return null;
    }

    @Override
    public List<Transfer> getTransfersByStatus(String status) {
        return null;
    }
}
