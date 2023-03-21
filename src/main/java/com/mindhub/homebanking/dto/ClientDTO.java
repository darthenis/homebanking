package com.mindhub.homebanking.dto;

import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.RoleType;

import java.util.Set;
import static java.util.stream.Collectors.toSet;

public class ClientDTO {


    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String address;

    private String country;

    private String tel;

    private String avatarUrl;

    private RoleType roleType;

    private Set<ClientLoanDTO> loans;

    private Set<AccountDTO> accounts;

    private Set<CardDTO> cards;


    public ClientDTO(Client client) {

        id = client.getId();

        firstName = client.getFirstName();

        lastName = client.getLastName();

        email = client.getEmail();

        address = client.getAddress();

        country = client.getCountry();

        tel = client.getTel();

        avatarUrl = client.getAvatarUrl();

        accounts = client.getAccounts().stream().map(AccountDTO::new).collect(toSet());

        loans = client.getClientLoans().stream().map(ClientLoanDTO::new).collect(toSet());

        cards = client.getCards().stream().map(CardDTO::new).collect(toSet());

        roleType = client.getRoleType();

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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public Set<AccountDTO> getAccounts() {
        return accounts;
    }

    public void setAccounts(Set<AccountDTO> accounts) {
        this.accounts = accounts;
    }

    public Set<ClientLoanDTO> getLoans(){ return loans; }

    public Set<CardDTO> getCards(){ return  cards;}

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String numberHouse) {
        this.country = numberHouse;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public RoleType getRoleType() {

        return roleType;

    }

    public void setRoleType(RoleType roleType) {
        roleType = roleType;
    }
}
