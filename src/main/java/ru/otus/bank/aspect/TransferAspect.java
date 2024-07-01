package ru.otus.bank.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import ru.otus.bank.entity.Account;
import ru.otus.bank.service.AccountService;

import java.math.BigDecimal;
import java.util.List;

@Aspect
public class TransferAspect {

    private final AccountService accountService;

    public TransferAspect(AccountService accountService) {
        this.accountService = accountService;
    }

    @Pointcut("execution(* ru.otus.bank.service.impl.AccountServiceImpl.makeTransfer(..)) && args(sourceId, destinationId, amount)")
    public void transferPointcut(long sourceId, long destinationId, BigDecimal amount) {}

    @AfterReturning(pointcut = "transferPointcut(sourceId, destinationId, amount)", returning = "result")
    public void afterTransfer(long sourceId, long destinationId, BigDecimal amount, boolean result) {
        if (result) {
            System.out.println("Transfer successful");

            List<Account> accounts = accountService.getAccounts();

            Account sourceAccount = findAccountById(accounts, sourceId);
            Account destinationAccount = findAccountById(accounts, destinationId);


            if (sourceAccount != null && destinationAccount != null) {
                System.out.println("Source Account: " + sourceAccount);
                System.out.println("Destination Account: " + destinationAccount);
                System.out.println("Transfer Amount: " + amount);

            } else {
                System.out.println("Failed to retrieve accounts");

            }
        } else {
            System.out.println("Transfer failed");

        }
    }

    private Account findAccountById(List<Account> accounts, long accountId) {
        for (Account account : accounts) {
            if (account.getId() == accountId) {
                return account;
            }
        }
        return null;
    }
}
