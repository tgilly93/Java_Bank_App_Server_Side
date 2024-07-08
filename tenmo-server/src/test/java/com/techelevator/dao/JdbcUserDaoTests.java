package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.RegisterUserDto;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;

public class JdbcUserDaoTests extends BaseDaoTests {
    protected static final User USER_1 = new User(1001, "user1", "user1", "USER");
    protected static final User USER_2 = new User(1002, "user2", "user2", "USER");
    private static final User USER_3 = new User(1003, "user3", "user3", "USER");
    protected static final Account ACCOUNT_1 = new Account();
    protected static final Account ACCOUNT_2 = new Account();
    protected static final Account ACCOUNT_3 = new Account();

    private JdbcUserDao userDao;
    private JdbcTransferDao transferDao;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        userDao = new JdbcUserDao(jdbcTemplate);
        transferDao = new JdbcTransferDao(jdbcTemplate, userDao);
        ACCOUNT_1.setBalance(BigDecimal.valueOf(1000.00));
        ACCOUNT_1.setAccountID(1001);
        ACCOUNT_1.setUserID(1001);
        ACCOUNT_2.setBalance(BigDecimal.valueOf(1000.00));
        ACCOUNT_2.setAccountID(1002);
        ACCOUNT_2.setUserID(1002);
        ACCOUNT_3.setBalance(BigDecimal.valueOf(1000.00));
        ACCOUNT_3.setAccountID(1003);
        ACCOUNT_3.setUserID(1003);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getUserByUsername_given_null_throws_exception() {
        userDao.getUserByUsername(null);
    }

    @Test
    public void getUserByUsername_given_invalid_username_returns_null() {
        Assert.assertNull(userDao.getUserByUsername("invalid"));
    }

    @Test
    public void getUserByUsername_given_valid_user_returns_user() {
        User actualUser = userDao.getUserByUsername(USER_1.getUsername());

        Assert.assertEquals(USER_1, actualUser);
    }

    @Test
    public void getUserById_given_invalid_user_id_returns_null() {
        User actualUser = userDao.getUserById(-1);

        Assert.assertNull(actualUser);
    }

    @Test
    public void getUserById_given_valid_user_id_returns_user() {
        User actualUser = userDao.getUserById(USER_1.getId());

        Assert.assertEquals(USER_1, actualUser);
    }

    @Test
    public void getUsers_returns_all_users() {
        List<User> users = userDao.getUsers();

        Assert.assertNotNull(users);
        Assert.assertEquals(3, users.size());
        Assert.assertEquals(USER_1, users.get(0));
        Assert.assertEquals(USER_2, users.get(1));
        Assert.assertEquals(USER_3, users.get(2));
    }

    @Test(expected = DaoException.class)
    public void createUser_with_null_username() {
        RegisterUserDto registerUserDto = new RegisterUserDto();
        registerUserDto.setUsername(null);
        registerUserDto.setPassword(USER_1.getPassword());
        userDao.createUser(registerUserDto);
    }

    @Test(expected = DaoException.class)
    public void createUser_with_existing_username() {
        RegisterUserDto registerUserDto = new RegisterUserDto();
        registerUserDto.setUsername(USER_1.getUsername());
        registerUserDto.setPassword(USER_3.getPassword());
        userDao.createUser(registerUserDto);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createUser_with_null_password() {
        RegisterUserDto registerUserDto = new RegisterUserDto();
        registerUserDto.setUsername(USER_3.getUsername());
        registerUserDto.setPassword(null);
        userDao.createUser(registerUserDto);
    }

    @Test
    public void createUser_creates_a_user() {
        RegisterUserDto user = new RegisterUserDto();
        user.setUsername("new");
        user.setPassword("USER");

        User createdUser = userDao.createUser(user);

        Assert.assertNotNull(createdUser);

        User retrievedUser = userDao.getUserByUsername(createdUser.getUsername());
        Assert.assertEquals(retrievedUser, createdUser);
    }

    //JDBCTRANSFERDAO TESTS

    @Test
    public void getAccountByID_given_valid_id_returns_account() {
        Account account = transferDao.getAccountByUserID(2001); // Assuming 2001 is a valid account ID in the test database

        Assert.assertNotNull(account);
        Assert.assertEquals(2001, account.getAccountID());
        Assert.assertEquals(new BigDecimal("1000.00"), account.getBalance()); // Adjust expected balance as necessary
    }

    @Test
    public void updateBalance_transfers_funds_correctly() {
        Transfer transfer = new Transfer();
        transfer.setFromAccountID(2001);
        transfer.setToAccountID(2002);
        transfer.setAmount(new BigDecimal("100.00"));

//        ACCOUNT_1.setBalance(BigDecimal.valueOf(1000.00));
//        ACCOUNT_1.setAccountID(1001);
//        ACCOUNT_1.setUserID(1001);
//        ACCOUNT_2.setBalance(BigDecimal.valueOf(1000.00));
//        ACCOUNT_2.setAccountID(1002);
//        ACCOUNT_2.setUserID(1002);
//        ACCOUNT_3.setBalance(BigDecimal.valueOf(1000.00));
//        ACCOUNT_3.setAccountID(1003);
//        ACCOUNT_3.setUserID(1003);

        transferDao.updateBalance(transfer);

        Account fromAccount = transferDao.getAccountByID(2001);
        Account toAccount = transferDao.getAccountByID(2002);

        Assert.assertEquals(new BigDecimal("900.00"), fromAccount.getBalance()); // Adjust expected balance as necessary
        Assert.assertEquals(new BigDecimal("1100.00"), toAccount.getBalance()); // Adjust expected balance as necessary
    }

    @Test
    public void getTransfers_returns_all_transfers_for_account() {
        List<Transfer> transfers = transferDao.getTransfers(1001); // Assuming 1001 is a valid user ID in the test database

        Assert.assertNotNull(transfers);
        Assert.assertEquals(3, transfers.size()); // Adjust the expected size based on your test data
        Assert.assertEquals(3001, transfers.get(0).getTransferID()); // Adjust expected transfer ID as necessary
    }

    @Test
    public void sendTransfer_creates_new_transfer() {
        Transfer transfer = transferDao.sendTransfer(2002, 2001, new BigDecimal("50.00"));

        Assert.assertNotNull(transfer);
        Assert.assertEquals(2001, transfer.getFromAccountID());
        Assert.assertEquals(2002, transfer.getToAccountID());
        Assert.assertEquals(new BigDecimal("50.00"), transfer.getAmount());
    }

    @Test
    public void requestTransfer_creates_new_transfer() {
        Transfer transfer = transferDao.requestTransfer(2002, 2001, new BigDecimal("50.00"));

        Assert.assertNotNull(transfer);
        Assert.assertEquals(2002, transfer.getFromAccountID());
        Assert.assertEquals(2001, transfer.getToAccountID());
        Assert.assertEquals(new BigDecimal("50.00"), transfer.getAmount());
    }

    @Test
    public void updateTransfer_updates_transfer_status() {
        Transfer transfer = transferDao.getTransferByID(3001); // Assuming 3001 is a valid transfer ID in the test database
        Transfer updatedTransfer = transferDao.updateTransfer(transfer, "approved");

        Assert.assertNotNull(updatedTransfer);
        Assert.assertEquals(3001, updatedTransfer.getTransferID());
        Assert.assertEquals("approved", updatedTransfer.getTransferStatusID()); // Adjust expected status as necessary
    }

    @Test
    public void getTransferByID_given_valid_id_returns_transfer() {
        Transfer transfer = transferDao.getTransferByID(3001); // Assuming 3001 is a valid transfer ID in the test database

        Assert.assertNotNull(transfer);
        Assert.assertEquals(3001, transfer.getTransferID());
        Assert.assertEquals(new BigDecimal("100.00"), transfer.getAmount()); // Adjust expected amount as necessary
    }

    @Test
    public void getPendingRequests_returns_all_pending_requests_for_user() {
        transferDao.sendTransfer(1002,1001, new BigDecimal(500));
        List<Transfer> transfers = transferDao.getPendingRequests(1001); // Assuming 1001 is a valid user ID in the test database

        Assert.assertNotNull(transfers);
        Assert.assertEquals(1, transfers.size()); // Adjust the expected size based on your test data

    }
}

