package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/transfer-requests")
public class TransferRequestController {

    private TransferDao transferDao;
    private UserDao userDao;

    @Autowired
    public TransferRequestController(TransferDao transferDao, UserDao userDao) {
        this.transferDao = transferDao;
        this.userDao = userDao;
    }

    //this needs to pass the user id's not the user names
    @PostMapping
    public Transfer requestTransfer(@Valid @RequestBody Transfer transferRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User fromUser = userDao.getUserByUsername(username);
        User toUser = userDao.getUserById(transferRequest.getToAccountID());

        if (fromUser == null || toUser == null) {
            throw new IllegalArgumentException("Invalid user IDs.");
        }

        transferDao.requestTransfer(toUser.getId(), fromUser.getId(), transferRequest.getAmount());

        return transferRequest;
    }

    //transferDao method is getPending request also.
    @GetMapping("/pending-requests")
    public List<Transfer> getPendingRequests() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userDao.getUserByUsername(username);

        if (user == null) {
            throw new IllegalArgumentException("Invalid user ID.");
        }

        List<Transfer> pendingRequests = transferDao.getPendingRequests(user.getId());

        return pendingRequests;
    }

    @GetMapping("/{transferRequestId}")
    public Transfer getTransferRequestById(@PathVariable Long transferRequestId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userDao.getUserByUsername(username);

        if (user == null) {
            throw new IllegalArgumentException("Invalid user ID.");
        }

        Transfer transferRequest = transferDao.getTransferByID(transferRequestId.intValue());

        if (transferRequest == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transfer request not found.");
        }

        return transferRequest;
    }

    @PostMapping("/{transferRequestId}/approve")
    public Transfer approveTransferRequest(@PathVariable Long transferRequestId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userDao.getUserByUsername(username);

        if (user == null) {
            throw new IllegalArgumentException("Invalid user ID.");
        }

        Transfer transferRequest = transferDao.getTransferByID(transferRequestId.intValue());

        if (transferRequest == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transfer request not found.");
        }

        if (!"PENDING".equals(transferRequest.getTransferStatusID())) {
            throw new IllegalStateException("Transfer request is already processed.");
        }

        transferRequest.setTransferStatusID(1);

        return transferRequest;
    }
}
    /*
    @PostMapping("/{transferId}/reject")
    public Transfer rejectTransfer(@PathVariable int transferId) {

        Transfer transfer = transferDao.getTransferByID(transferId);

        if (transfer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transfer not found.");
        }

        try {

            transferDao.updateTransferStatus(transferId, "REJECTED");


            int accountId = transfer.getFromAccountID();
            BigDecimal amount = transfer.getAmount();

            AccountDao.updateAccount(accountId, amount.negate());


            transfer.setTransferStatusID("REJECTED");

        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to reject transfer.", e);
        }

        return transfer;
    }
*/




