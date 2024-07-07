package com.techelevator.tenmo.controller;



import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;



public class AccountController {

    private final AccountDao accountDao;
    private final UserDao userDao;

    public AccountController(AccountDao accountDao, UserDao userDao) {
        this.accountDao = accountDao;
        this.userDao = userDao;
    }

    @GetMapping("/balance")
    public BigDecimal getAccountBalance() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userDao.getUserByUsername(username);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + username);
        }

        Account account = accountDao.getAccountByID(user.getId());

        if (account == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found for user: " + username);
        }

        return account.getBalance();
    }
}
