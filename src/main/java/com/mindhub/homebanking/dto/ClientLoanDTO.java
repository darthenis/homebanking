package com.mindhub.homebanking.dto;

import com.mindhub.homebanking.models.ClientLoan;

public class ClientLoanDTO {

    private Long id;

    private Long loanId;

    private String name;

    private Double amount;

    private int payment;

    public ClientLoanDTO(){}

    public ClientLoanDTO(ClientLoan clientLoan){

        id = clientLoan.getId();

        loanId = clientLoan.getLoan().getId();

        name = clientLoan.getLoan().getName();

        amount = clientLoan.getAmount();

        payment = clientLoan.getPayment();

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
