package com.mindhub.homebanking.services.impl;

import com.mindhub.homebanking.dto.LoanApplicationDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.ClientLoan;
import com.mindhub.homebanking.models.Loan;
import com.mindhub.homebanking.repositories.LoanRepository;
import com.mindhub.homebanking.services.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Objects;
import java.util.Optional;

@Service
public class LoanServiceImpl implements LoanService {

    @Autowired
    LoanRepository loanRepository;

    @Autowired
    ClientServiceImpl clientService;

    @Autowired
    AccountServiceImpl accountService;

    @Autowired
    ClientLoanServiceImpl clientLoanService;

    @Override
    public void save(Loan loan) {
        loanRepository.save(loan);
    }

    @Override
    public void loanApplication(Authentication authentication, LoanApplicationDTO loanApplicationDTO) throws Exception {

        if(loanApplicationDTO.getLoanId() == null) throw new IllegalArgumentException("Missing loanId");

        if(loanApplicationDTO.getAmount() == null) throw new IllegalArgumentException("Missing amount");

        if(loanApplicationDTO.getAccountNumber() == null) throw new IllegalArgumentException("Missing payment");

        if(loanApplicationDTO.getAmount() < 1000) throw new IllegalArgumentException("The mount must be over than 1000");

        Optional<Loan> loan = loanRepository.findById(loanApplicationDTO.getLoanId());

        if(loan.isEmpty()) throw new EntityNotFoundException("The loan doesn't exists");

        if(loanApplicationDTO.getAmount() > loan.get().getMaxAmount()) throw new IllegalArgumentException("the amount must not exceed the max amount");

        Client client = clientService.findByEmail(authentication.getName());

        if(client.getAccounts().stream().noneMatch(account -> account.getNumber().equals(loanApplicationDTO.getAccountNumber()))) throw new EntityNotFoundException("The account does not belong to you");

        if(loan.get().getPayments().stream().noneMatch(payment -> payment == loanApplicationDTO.getPayment())) throw new IllegalArgumentException("The payment are not available");

        if(client.getLoans().stream().anyMatch(clientLoan -> Objects.equals(clientLoan.getName(), loan.get().getName()))) throw new Exception("Loan already adquired");

        Optional<Account> account = accountService.findByNumber(loanApplicationDTO.getAccountNumber());

        Double interest = (loanApplicationDTO.getAmount() / 100) * loan.get().getInterest();

        ClientLoan clientLoan = new ClientLoan(loanApplicationDTO.getAmount() + interest, loanApplicationDTO.getPayment(), client, loan.get());

        client.addClientLoan(clientLoan);

        loan.get().addClientLoan(clientLoan);

        account.get().setBalance(account.get().getBalance() + loanApplicationDTO.getAmount());

        clientLoanService.save(clientLoan);

        accountService.save(account.get());

        loanRepository.save(loan.get());

        clientService.save(client);

    }
}
