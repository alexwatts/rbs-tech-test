package com.rbs.transfer.service;

import com.rbs.transfer.model.Account;
import com.rbs.transfer.model.AccountIdentifier;
import com.rbs.transfer.model.Money;
import com.rbs.transfer.model.Transfer;
import com.rbs.transfer.service.exception.AccountDetailsInvalidException;
import com.rbs.transfer.service.exception.InsufficentFundsException;
import javafx.util.Pair;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class BankingService {

    private ConcurrentHashMap<AccountIdentifier, Pair<Account, Lock>> accounts = new ConcurrentHashMap<>();

    public Account createAccount(Account account) {
        return getOrCreateAccount(account).getKey();
    }

    public Account getAccount(AccountIdentifier accountIdentifier) {
        return accounts.get(accountIdentifier).getKey();
    }

    public Transfer transfer(Transfer transfer) throws InsufficentFundsException, AccountDetailsInvalidException {
        validateTransfer(transfer);

        List<Pair<AccountIdentifier, Money>> orderedTransactions = getTransactionsToMakeOrderedByAccountId(
                transfer.getSourceAccountIdentifier(),
                transfer.getDestinationAccountIdentifier(),
                transfer.getTransferValue());

        validateSourceAccountHasFunds(accounts.get(transfer.getSourceAccountIdentifier()).getKey(), transfer.getTransferValue());

        makeTransactions(orderedTransactions);

        return transfer;
    }

    private Pair<Account, Lock> getOrCreateAccount(final Account account) {
        return accounts.computeIfAbsent(
                account.getAccountIdentifier(),
                ac -> new Pair<>(
                        new Account(account.getAccountIdentifier(),account.getBalance()),
                        new ReentrantLock()));
    }

    private void makeTransactions(List<Pair<AccountIdentifier, Money>> transactions) {
        transactions.forEach(
                transaction ->
                        makeTransaction(
                                accounts.get(transaction.getKey()).getKey(),
                                accounts.get(transaction.getKey()).getValue(),
                                transaction.getValue())
        );
    }

    private void makeTransaction(Account account, Lock lock, Money transactionValue) {
        lock.lock();
        try {
            Account newAccount = new Account(account.getAccountIdentifier(), account.getBalance().add(transactionValue));
            for (;;) {
                if (accounts.replace(account.getAccountIdentifier(), new Pair<>(account, lock), new Pair<>(newAccount, lock))) {
                    break;
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private void validateTransfer(Transfer transfer) throws AccountDetailsInvalidException {
        Pair<Account, Lock> sourceAccount = accounts.get(transfer.getSourceAccountIdentifier());
        Pair<Account, Lock>  destinationAccount = accounts.get(transfer.getDestinationAccountIdentifier());

        if (sourceAccount == null) {
            messageAccountNotFound(transfer.getSourceAccountIdentifier());
        }

        if (destinationAccount == null) {
            messageAccountNotFound(transfer.getDestinationAccountIdentifier());
        }
    }

    private void validateSourceAccountHasFunds(Account sourceAccount, Money transferValue) throws InsufficentFundsException {
        if (sourceAccount.getBalance().subtract(transferValue).getValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InsufficentFundsException("Insufficient Funds");
        }
    }

    private void messageAccountNotFound(AccountIdentifier accountIdentifier) throws AccountDetailsInvalidException {
        throw new AccountDetailsInvalidException(String.format("This account was not found. %s/%s", accountIdentifier.getSortCode(), accountIdentifier.getAccountNumber()));
    }

    private List<Pair<AccountIdentifier, Money>> getTransactionsToMakeOrderedByAccountId(AccountIdentifier sourceAccountIdentifier, AccountIdentifier destinationAccountIdentifier, Money transactionAmount) {
        List<Pair<AccountIdentifier, Money>> transactionsToMake =
                Arrays.asList(
                        new Pair<>(sourceAccountIdentifier, new Money(transactionAmount.getCurrency(), transactionAmount.getValue().negate())),
                        new Pair<>(destinationAccountIdentifier, new Money(transactionAmount.getCurrency(), transactionAmount.getValue()))
                );
        transactionsToMake.sort(Comparator.comparing((Pair transaction) -> ((AccountIdentifier)transaction.getKey()).getAccountNumber()));
        return transactionsToMake;
    }

}
