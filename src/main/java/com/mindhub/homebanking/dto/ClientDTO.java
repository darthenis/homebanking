package com.mindhub.homebanking.dto;

import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.Client;

import java.util.Set;
import static java.util.stream.Collectors.toSet;

public class ClientDTO {


    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private Set<ClientLoanDTO> loans;

    private Set<AccountDTO> accounts;

    private Set<CardDTO> cards;


    public ClientDTO(Client client) {

        id = client.getId();

        firstName = client.getFirstName();

        lastName = client.getLastName();

        email = client.getEmail();

        accounts = client.getAccounts().stream().map(AccountDTO::new).collect(toSet());

        loans = client.getClientLoans().stream().map(ClientLoanDTO::new).collect(toSet());

        cards = client.getCards().stream().map(CardDTO::new).collect(toSet());

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<AccountDTO> getAccounts() {
        return accounts;
    }

    public void setAccounts(Set<AccountDTO> accounts) {
        this.accounts = accounts;
    }

    public Set<ClientLoanDTO> getLoans(){ return loans; }

    public Set<CardDTO> getCards(){ return  cards;}
}
