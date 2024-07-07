package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
@RestController
@RequestMapping("/transfers")
public class TransferController {

    private TransferDao transferDao;
    private UserDao userDao;

    @Autowired
    public TransferController(TransferDao transferDao, UserDao userDao ) {
        this.transferDao = transferDao;
        this.userDao = userDao;
    }
// " /transfers/123?toAccountId=456&transferAmount=100.00"
 /*   @ResponseStatus(HttpStatus.CREATED)
    @PostMapping ( "/transfers/{id}?/toAccountId=?&transferAmount=?")

    public Transfer sendTransfer(@PathVariable("id") int userId, @RequestParam int toAccountId, @RequestParam BigDecimal transferAmount) {
        try {
           User user = userDao.getUserById(userId);
           if (user == null) {
               throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
           }
           return transferDao.sendTransfer(toAccountId, user, transferAmount);

        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to process transfer", e);
        }

    }
*/
@PostMapping("/sendTransfer")
public Transfer sendTransfer(@Valid @RequestBody Transfer transferRequest) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();
    User fromUser = userDao.getUserByUsername(username);
    User toUser = userDao.getUserById(transferRequest.getToAccountID());

    if (fromUser == null || toUser == null) {
        throw new IllegalArgumentException("Invalid user ID.");
    }

    Transfer sentTransfer = transferDao.sendTransfer(toUser.getId(), fromUser.getId(), transferRequest.getAmount());

    return sentTransfer;
}
    /*
    @GetMapping(path = "/users/{id}/transfers")

    public List<Transfer> listOfTransfers(@PathVariable("id") int id) {
       List<Transfer> transfers = transferDao.getTransfers();

        if (transfers == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        } else {
            return transfers;
        }
    }
    */

    //this needs the user id not user name
    @GetMapping("/transfers")
    public List<Transfer> getTransfers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userDao.getUserByUsername(username);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        int userId = user.getId();

        return transferDao.getTransfers(userId);
    }
    @GetMapping("/{transferId}")
    public Transfer getTransferById(@PathVariable int transferId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userDao.getUserByUsername(username);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Transfer transfer = transferDao.getTransferByID(transferId);

        if (transfer != null && transfer.getTransferID() != user.getId()) {
            throw new RuntimeException("Transfer not found for the authenticated user");
        }

        return transfer;
    }

}
