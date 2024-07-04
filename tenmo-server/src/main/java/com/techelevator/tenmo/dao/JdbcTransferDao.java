package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.math.BigDecimal;
import java.util.ArrayList;
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
       } catch (CannotGetJdbcConnectionException e) {
           throw new DaoException("Unable to connect to server or database", e);
       } catch (DataIntegrityViolationException e) {
           throw new DaoException("Data integrity violation", e);
       }

       return account;
    }

    /*
       should take in the transfer and run two different sql calls
     */

    @Override
    public void updateBalance(Transfer transfer) {
       Account fromAccount = getAccountByID(transfer.getFromAccountID());
       Account toAccount = getAccountByID(transfer.getToAccountID());
       BigDecimal fromResult = fromAccount.getBalance().subtract(transfer.getAmount());
       BigDecimal toResult = toAccount.getBalance().add(transfer.getAmount());
       fromAccount.setBalance(fromResult);
       toAccount.setBalance(toResult);

        String sqlToAccount = "UPDATE public.account\n" +
                "\tSET account_id=?, user_id=?, balance=?\n" +
                "\tWHERE account_id = ?;";
        String sqlFromAccount = "UPDATE public.account\n" +
                "\tSET account_id=?, user_id=?, balance=?\n" +
                "\tWHERE account_id = ?;";
        try{
            int rowsAffected = jdbcTemplate.update(sqlFromAccount,fromAccount.getAccountID(),fromAccount.getUserID()
                    ,fromAccount.getBalance(),fromAccount.getAccountID());
            if (rowsAffected == 0) {
                throw new DaoException("Zero rows affected, expected at least one");
            }
            rowsAffected = jdbcTemplate.update(sqlToAccount,toAccount.getAccountID(),toAccount.getUserID(),
                    toAccount.getBalance(), toAccount.getAccountID());
            if (rowsAffected == 0) {
                throw new DaoException("Zero rows affected, expected at least one");
            }
        }catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }


    }

    private Account mapToAccount(SqlRowSet result) {
        Account account = new Account();
        account.setAccountID(result.getInt("account_id"));
        account.setUserID(result.getInt("user_id"));
        account.setBalance(result.getBigDecimal("balance"));


        return  account;
    }

    @Override
    public List<Transfer> getTransfers(int userId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount\n" +
                "\tFROM public.transfer\n" +
                "\tjoin tenmo_user on user_id = account_from\n" +
                "\twhere user_id = ?;";
        try{
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            if(results.next()){
                Transfer transfer = mapToTransfer(results);
                transfers.add(transfer);
            }
        }catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }

        return transfers;
    }

    /*Method need to send money to user
       default status of transfer is approved
       needs to update the transfer_status table,
       transfer_type table and then transfer table

       needed the id from logged user, id of user sending funds, and amount
        */
        // TODO- SEND TRANSFER METHOD
    @Override
    public Transfer sendTransfer(int sendToId, int userId, BigDecimal amount) {
        return null;
    }


   // TODO- Request TRANSFER METHOD
        /*Method need to send request to user to get funds
        default status of transfer is pending
        needs to update the transfer_status table,
        transfer_type table and then transfer table

        needed the id from logged user, id of user sending funds, and amount
         */
    @Override
    public Transfer requestTransfer(int requestFromID, int userId, BigDecimal amount) {
        return null;
    }

    //TODO - UPDATE TRANSFER METHOD
    /*
        METHOD  needs to take in the transfer id
        update the status of the transfer(approved,rejected)
        then update the transfer_status table
        depending on status change need to updateAccount method
     */


    @Override
    public Transfer getTransferByID(int id) {
       Transfer transfer = new Transfer();
       String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount\n" +
               "\tFROM public.transfer\n" +
               "\tWHERE transfer_id = ?;";
       try{
           SqlRowSet result = jdbcTemplate.queryForRowSet(sql,id);
           if(result.next()){
               transfer = mapToTransfer(result);
           }
       }catch (CannotGetJdbcConnectionException e) {
           throw new DaoException("Unable to connect to server or database", e);
       } catch (DataIntegrityViolationException e) {
           throw new DaoException("Data integrity violation", e);
       }
       return transfer;
    }

    @Override
    public List<Transfer> getTransfersByStatus(String status, int userID) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount\n" +
                "\tFROM public.transfer\n" +
                "\tjoin tenmo_user on user_id = account_from\n" +
                "\tjoin transfer_status on transfer.transfer_status_id = transfer_status.transfer_status_id\n" +
                "where user_id = ? and transfer_status_desc = ?;";
        try{
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql,userID,status);
            if(results.next()){
                Transfer transfer = mapToTransfer(results);
                transfers.add(transfer);
            }
        }catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }

        return transfers;
    }

    private Transfer mapToTransfer(SqlRowSet results) {
        Transfer transfer = new Transfer();
        transfer.setAmount(results.getBigDecimal("amount"));
        transfer.setFromAccountID(results.getInt("account_from"));
        transfer.setToAccountID(results.getInt("account_to"));
        transfer.setTransferStatusID(results.getInt("transfer_status_id"));
        transfer.setTransferTypeID(results.getInt("transfer_type_id"));
        transfer.setTransferID(results.getInt("transfer_id"));

        return transfer;

    }

}
