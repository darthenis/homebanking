package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dto.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.services.impl.AccountServiceImpl;
import com.mindhub.homebanking.services.impl.ClientServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;



@RestController
@RequestMapping("/api")
public class AccountController {

    @Autowired
    ClientServiceImpl clientService;

    @Autowired
    AccountServiceImpl accountService;
    @Autowired
    private AccountRepository accountRepository;

    @PostMapping("/clients/current/accounts")
    public ResponseEntity<?> createAccount(Authentication authentication, @RequestParam AccountType accountType){

        Client client = clientService.findByEmail(authentication.getName());

        try{

            accountService.createAccount(client, accountType);

            return new ResponseEntity<>(HttpStatus.CREATED);

        }catch(AccessDeniedException accessDeniedException){

            return new ResponseEntity<>(accessDeniedException.getMessage(), HttpStatus.FORBIDDEN);

        }

    }

    @GetMapping("/clients/current/accounts")
    public List<AccountDTO> getAccounts(Authentication authentication) {

        return accountService.getAccountsCurrent(authentication).stream().map(AccountDTO::new).collect(Collectors.toList());

    }

    @GetMapping("/accounts")
    public List<Account> getAllAccounts(){

        return accountService.getAllAccounts();

    }

    @GetMapping("clients/current/accounts/{id}")
    public ResponseEntity<?> getAccount(@PathVariable Long id, Authentication authentication){

        try{

            return new ResponseEntity<>(new AccountDTO(accountService.getAccount(authentication, id)), HttpStatus.OK);

        }catch(EntityNotFoundException entityNotFoundException){

            return new ResponseEntity<>(entityNotFoundException.getMessage(), HttpStatus.FORBIDDEN);

        }


    }


    @DeleteMapping("/clients/current/accounts/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long id, Authentication authentication){

        try{

            accountService.deleteAccount(authentication, id);

            return new ResponseEntity<>(HttpStatus.OK);

        }
        catch (AccessDeniedException accessDeniedException){

            return new ResponseEntity<>(accessDeniedException.getMessage(), HttpStatus.FORBIDDEN);
        }



    }

}
