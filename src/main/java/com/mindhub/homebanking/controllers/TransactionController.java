package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dto.PayApplicationDTO;
import com.mindhub.homebanking.dto.TransactionDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @Autowired
    ClientService clientService;

    @Transactional
    @PostMapping("/clients/current/transactions")
    public ResponseEntity<?> createTransaction( Authentication authentication,
                                                @RequestParam("origin") String numberOrigin,
                                                @RequestParam("destination") String numberDestination,
                                                @RequestParam Double amount){


        if(numberOrigin == null) return new ResponseEntity<>("Missing numberOrigin", HttpStatus.FORBIDDEN);

        if(numberDestination == null) return new ResponseEntity<>("Missing numberDestination", HttpStatus.FORBIDDEN);

        if(amount == null) return new ResponseEntity<>("Missing amount", HttpStatus.FORBIDDEN);

        if(amount < 1) return new ResponseEntity<>("The amount can't be less than 1", HttpStatus.FORBIDDEN);

        try{

            transactionService.createTransaction(authentication, numberOrigin, numberDestination, amount);

            return new ResponseEntity<>(HttpStatus.CREATED);

        }catch(Exception exception){

            return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);

        }


    }

    @CrossOrigin
    @Transactional
    @PostMapping("/transactions/pay")
    public ResponseEntity<?> createPay(@RequestBody PayApplicationDTO payApplicationDTO){

        try{

            transactionService.createPay(payApplicationDTO);
            return new ResponseEntity<>("Transaction sucessfully", HttpStatus.OK);

        }catch (Exception exception){

            return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
        }

    }

    @GetMapping("clients/current/accounts/{id}/transactions")
    public ResponseEntity<?> filterTransactions(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateThru,
                                                @PathVariable Long id,
                                                Authentication authentication) {

        try{

            List<TransactionDTO> transactionDTOS = transactionService.filterTransactions(authentication, dateFrom, dateThru, id).stream().map(TransactionDTO::new).collect(Collectors.toList());

            return new ResponseEntity<>(transactionDTOS, HttpStatus.OK);

        }catch(EntityNotFoundException entityNotFoundException){

            return new ResponseEntity<>(entityNotFoundException.getMessage(), HttpStatus.FORBIDDEN);

        }

    }


    @GetMapping("/clients/current/accounts/{id}/transactions/pdf")
    public void getPdf(HttpServletResponse response,
                       Authentication authentication,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateThru,
                                    @PathVariable Long id) throws IOException, ParseException {


        Client client = clientService.findByEmail(authentication.getName());

        Optional<Account> selectedAccount = client.getAccounts().stream().filter(account -> account.getId() == id).findAny();

        if(selectedAccount.isEmpty()) response.sendError(403, "account not found");

        List<Transaction> transactions = selectedAccount.get().getTransactions().stream().sorted((p1, p2)->p2.getId().compareTo(p1.getId())).collect(Collectors.toList());

        if(dateFrom != null && dateThru == null) {

                transactions = transactions.stream().filter(transaction -> transaction.getDate().isAfter(dateFrom) || transaction.getDate().equals(dateFrom)).collect(Collectors.toList());

        }

        if(dateThru != null && dateFrom != null) {

                transactions = transactions.stream().filter(transaction -> (transaction.getDate().isBefore(dateThru) && transaction.getDate().isAfter(dateFrom)) || transaction.getDate().equals(dateFrom) || transaction.getDate().equals(dateThru) ).collect(Collectors.toList());

        }

        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=transactions_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        transactionService.UserPDFExporter(transactions);
        transactionService.export(response, dateFrom, dateThru, selectedAccount.get().getNumber(), client.getFirstName() + " " + client.getLastName(), selectedAccount.get().getBalance());

    }
}
