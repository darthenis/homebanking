package com.mindhub.homebanking.controllers;


import com.mindhub.homebanking.dto.LoanApplicationDTO;
import com.mindhub.homebanking.dto.LoanBaseDTO;
import com.mindhub.homebanking.dto.LoanDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.ClientLoan;
import com.mindhub.homebanking.models.Loan;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientLoanRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.LoanRepository;
import com.mindhub.homebanking.services.LoanService;
import com.mindhub.homebanking.services.impl.ClientServiceImpl;
import com.mindhub.homebanking.services.impl.LoanServiceImpl;
import com.sendgrid.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class loanController {

    @Autowired
    LoanRepository loanRepository;

    @Autowired
    LoanServiceImpl loanService;


    @RequestMapping("/loans")
    public List<LoanBaseDTO> getLoans(){

      return loanRepository.findAll().stream().map(LoanBaseDTO::new).collect(Collectors.toList());

    }

    @PostMapping("/loans")
    public ResponseEntity<?> createLoan(@RequestParam String name,
                                        @RequestParam Double maxAmount,
                                        @RequestParam List<Integer> payments,
                                        @RequestParam Integer interest){

            if(name == null) return new ResponseEntity<>("missing name", HttpStatus.FORBIDDEN);
            if(maxAmount == null) return new ResponseEntity<>("missing max amount", HttpStatus.FORBIDDEN);
            if(payments == null || payments.size() == 0) return new ResponseEntity<>("missing payments", HttpStatus.FORBIDDEN);
            if(interest == null) return new ResponseEntity<>("missing interest", HttpStatus.FORBIDDEN);

            Loan loan = new Loan(name, maxAmount, payments, interest);

            loanService.save(loan);

            return new ResponseEntity<>("Loan created successfully", HttpStatus.CREATED);
    }


    @Transactional  
    @PostMapping(path="/clients/current/loans")
    public ResponseEntity<?> loanApplication(Authentication authentication,
                                             @RequestBody LoanApplicationDTO loanApplicationDTO){
        try {
            loanService.loanApplication(authentication, loanApplicationDTO);
            return new ResponseEntity<>("Loan applicated succesfully", HttpStatus.CREATED);
        }catch(Exception exception){

            return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);

        }
    }

}
