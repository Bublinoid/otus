package ru.otus.bank.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.bank.dao.AccountDao;
import ru.otus.bank.entity.Account;
import ru.otus.bank.entity.Agreement;
import ru.otus.bank.service.exception.AccountException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    AccountDao accountDao;

    @InjectMocks
    AccountServiceImpl accountServiceImpl;


    @Test
    public void testTransfer() {
        Account sourceAccount = new Account();
        sourceAccount.setAmount(new BigDecimal(100));

        Account destinationAccount = new Account();
        destinationAccount.setAmount(new BigDecimal(10));

        when(accountDao.findById(eq(1L))).thenReturn(Optional.of(sourceAccount));
        when(accountDao.findById(eq(2L))).thenReturn(Optional.of(destinationAccount));

        accountServiceImpl.makeTransfer(1L, 2L, new BigDecimal(10));

        if (!sourceAccount.getAmount().equals(new BigDecimal(90))) {
            fail("Expected source account amount to be 90, but was " + sourceAccount.getAmount());
        }

        if (!destinationAccount.getAmount().equals(new BigDecimal(20))) {
            fail("Expected destination account amount to be 20, but was " + destinationAccount.getAmount());
        }
    }

    @Test
    public void testSourceNotFound() {
        when(accountDao.findById(any())).thenReturn(Optional.empty());

        try {
            accountServiceImpl.makeTransfer(1L, 2L, new BigDecimal(10));
            fail("Expected AccountException to be thrown");
        } catch (AccountException e) {
            assertEquals("No source account", e.getLocalizedMessage());
        }
    }

    @Test
    public void testTransferWithVerify() {
        Account sourceAccount = new Account();
        sourceAccount.setAmount(new BigDecimal(100));
        sourceAccount.setId(1L);

        Account destinationAccount = new Account();
        destinationAccount.setAmount(new BigDecimal(10));
        destinationAccount.setId(2L);

        when(accountDao.findById(eq(1L))).thenReturn(Optional.of(sourceAccount));
        when(accountDao.findById(eq(2L))).thenReturn(Optional.of(destinationAccount));

        accountServiceImpl.makeTransfer(1L, 2L, new BigDecimal(10));

        verify(accountDao).save(argThat(argument -> argument.getId().equals(1L) && argument.getAmount().equals(new BigDecimal(90))));
        verify(accountDao).save(argThat(argument -> argument.getId().equals(2L) && argument.getAmount().equals(new BigDecimal(20))));
    }

    @Test
    public void testAddedAccount() {
        Account account = new Account();
        Agreement agreement = new Agreement();
        String accountNumber = "test";
        Integer type = 10;
        BigDecimal amount = new BigDecimal(10000);

        when(accountDao.save(argThat(arg -> arg.getNumber().equals(accountNumber)
                && arg.getType().equals(type)
                && arg.getAmount().equals(amount)))).thenReturn(account);

        accountServiceImpl.addAccount(agreement, accountNumber, type, amount);
    }


    @Test
    public void testGetAccount() {
        Agreement agreement = new Agreement();
        agreement.setId(1L);

        List<Account> expectedAccounts = new ArrayList<>();

        when(accountDao.findByAgreementId(agreement.getId())).thenReturn(expectedAccounts);

        List<Account> actualAccounts = accountServiceImpl.getAccounts(agreement);

        assertEquals(expectedAccounts, actualAccounts);

        verify(accountDao).findByAgreementId(agreement.getId());
    }

    @Test
    public void testChangeNoAccount() {
        Account account = new Account();
        account.setAmount(new BigDecimal(100));
        account.setId(1L);

        try {
            accountServiceImpl.charge(account.getId(), BigDecimal.TEN);
            fail("Expected AccountException to be thrown");
        } catch (AccountException e) {
        }
    }
}
