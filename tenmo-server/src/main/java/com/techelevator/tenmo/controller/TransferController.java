package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

public class TransferController {

    private TransferDao transferDao;
    private UserDao userDao;

    public TransferController(TransferDao transferDao, UserDao userDao ) {
        this.transferDao = transferDao;
        this.userDao = userDao;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping ( "/transfers/{id}")

    public Transfer sendTransfer(@PathVariable("id") int userId, @Valid @RequestBody FromAccount fromAccount) {
        try {
           User user = userDao.getUserById(userId);
           if (user == null) {
               throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
           }
           return transferDao.sendTransfer(fromAccount, user);

        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to process transfer", e);
        }

    }
    @GetMapping(path = "/users/{id}/transfers")

    public List<Transfer> listOfTransfers(@PathVariable("id") int userId) {
        List<Transfer> transfers = transferDao.getTransfersByUser(userId);

        if (transfers == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        } else {
            return transfers;
        }
    }
}
