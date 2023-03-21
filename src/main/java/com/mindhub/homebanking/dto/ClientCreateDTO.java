package com.mindhub.homebanking.dto;

import com.mindhub.homebanking.models.RoleType;

public class ClientCreateDTO {

    private String firstName;

    private String LastName;

    private String email;

    private String password;

    private RoleType roleType;


    public ClientCreateDTO(String firstName, String lastName, String email, String password, RoleType roleType) {
        this.firstName = firstName;
        LastName = lastName;
        this.email = email;
        this.password = password;
        this.roleType = roleType;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }
}
