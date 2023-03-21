package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dto.LoanApplicationDTO;
import com.mindhub.homebanking.models.Loan;
import org.springframework.security.core.Authentication;

import javax.persistence.EntityNotFoundException;

public interface LoanService {

    void save(Loan loan);

    void loanApplication(Authentication authentication, LoanApplicationDTO loanApplicationDTO) throws IllegalArgumentException, EntityNotFoundException, Exception;

}
