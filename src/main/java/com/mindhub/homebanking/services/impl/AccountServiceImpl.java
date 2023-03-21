package com.mindhub.homebanking.services.impl;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.utils.MathsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Lazy
    @Autowired
    ClientServiceImpl clientService;

    @Override
    public Account createFirstAccount() {

        String number = this.generateNumberAccount();

        return new Account(number, LocalDate.now(), 0.0, AccountType.SAVING);

    }

    @Override
    public void save(Account account) {

        accountRepository.save(account);
    }

    @Override
    public void createAccount(Client client, AccountType accountType) throws AccessDeniedException {

        if(client.getAccounts().stream().filter(account -> !account.getIsDisabled()).collect(Collectors.toSet()).size() == 3) {

            throw new AccessDeniedException("client already have 3 accounts");

        }

        String number = this.generateNumberAccount();

        Account account = new Account(number, LocalDate.now(), 0.0, accountType);

        client.addAccount(account);

        clientService.save(client);

        accountRepository.save(account);


    }

    public String generateNumberAccount(){

        String number;

        do{

            int random = MathsUtils.random(99999999, 1);

            number = "VIN-"+String.format("%08d", random);

        }while(accountRepository.existsByNumber(number));

        return number;

    }

    @Override
    public Set<Account> getAccountsCurrent(Authentication authentication) {

        Client client = clientService.findByEmail(authentication.getName());

        return client.getAccounts().stream().filter(account -> !account.getIsDisabled()).collect(Collectors.toSet());

    }

    @Override
    public Account getAccount(Authentication authentication, Long id) throws EntityNotFoundException {

        Client client = clientService.findByEmail(authentication.getName());

        Optional<Account> account = accountRepository.findById(id);

        if(account.isEmpty() || client.getId() != account.get().getClient().getId()){

            throw new EntityNotFoundException("Account not found");
        }

        return account.get();

    }

    @Override
    public void deleteAccount(Authentication authentication, Long id) throws EntityNotFoundException {

        Client client = clientService.findByEmail(authentication.getName());

        Account clientAccount = client.getAccounts().stream().filter(account -> account.getId() == id).findAny().orElse(null);

        if(clientAccount == null){

            throw new EntityNotFoundException("Account not found");

        }

        clientAccount.setIsDisabled(true);

        accountRepository.save(clientAccount);

    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public Optional<Account> findByNumber(String number) {

        return accountRepository.findByNumber(number);
    }

}
