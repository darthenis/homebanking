package com.mindhub.homebanking.dto;

import autovalue.shaded.org.jetbrains.annotations.NotNull;

public class LoanApplicationDTO {

    @NotNull
    Long loanId;

    @NotNull
    Double amount;

    @NotNull
    Integer payment;

    @NotNull
    String accountNumber;


    public LoanApplicationDTO(Long loanId, Double amount, Integer payment, String accountNumber) {
        this.loanId = loanId;
        this.amount = amount;
        this.payment = payment;
        this.accountNumber = accountNumber;
    }

    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public int getPayment() {
        return payment;
    }

    public void setPayment(int payment) {
        this.payment = payment;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
