package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/transfer-requests")
public class TransferRequestController {

    private final TransferDao transferDao;
    private final UserDao userDao;

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

        transferDao.requestTransfer(toUser.getUsername(), fromUser.getUsername(), transferRequest.getAmount());
        return transferRequest;
    }

    //transferDao method is getPending request also.
    @GetMapping
    public List<Transfer> getPendingRequests()  {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userDao.getUserByUsername(username);

        return (List<Transfer>) transferDao.getPendingRequestsByUserId(user.getId());
    }

    @GetMapping("/{transferRequestId}")
    public Transfer getTransferRequestById(@PathVariable Long transferRequestId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userDao.getUserByUsername(username);

        return transferDao.getTransferByID(transferRequestId.intValue(), username);
    }

    @PostMapping("/{transferRequestId}approve")
    public Transfer approveTransferRequest(@PathVariable Long transferRequestId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userDao.getUserByUsername(username);

        return transferDao.approveTransferRequest(transferRequestId.intValue(), user.getId());
    }

    @PostMapping("/{transferRequestId}/reject")
    public Transfer rejectTransferRequest(@PathVariable Long transferRequestId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userDao.getUserByUsername(username);

        return transferDao.rejectTransferRequest(transferRequestId.intValue(), user.getId());
    }
}


