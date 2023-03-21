package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import com.mindhub.homebanking.models.Client;
import org.springframework.security.core.Authentication;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AccountService {

    Account createFirstAccount();

    void save(Account account);

    void createAccount(Client client, AccountType accountType) throws AccessDeniedException;

    Set<Account> getAccountsCurrent(Authentication authentication);

    Account getAccount(Authentication authentication, Long id) throws EntityNotFoundException;

    void deleteAccount(Authentication authentication, Long id) throws EntityNotFoundException;

    List<Account> getAllAccounts();

    Optional<Account> findByNumber(String number);

    String generateNumberAccount();

}
