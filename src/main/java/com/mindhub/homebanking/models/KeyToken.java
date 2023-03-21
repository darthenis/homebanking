package com.mindhub.homebanking.models;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.security.Key;
import java.time.LocalDateTime;

@Entity
public class KeyToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    private String token;

    private LocalDateTime expiredAt;

    @OneToOne(mappedBy = "keyToken")
    private Client client;

    public KeyToken(){}

    public KeyToken(String token, LocalDateTime expiredAt){

        this.token = token;
        this.expiredAt = expiredAt;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return "KeyToken{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", expiredAt=" + expiredAt +
                ", client=" + client +
                '}';
    }
}
