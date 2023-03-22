package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.KeyToken;

import java.io.IOException;

public interface KeyTokenService {

    KeyToken generateAndSendKeyToken(String email, Client newClient) throws Exception;

    KeyToken findByToken(String token);

    boolean existsByToken(String token);

}
