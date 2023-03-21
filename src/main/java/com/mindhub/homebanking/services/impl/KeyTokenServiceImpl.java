package com.mindhub.homebanking.services.impl;

import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.KeyToken;
import com.mindhub.homebanking.repositories.KeyTokenRepository;
import com.mindhub.homebanking.services.KeyTokenService;
import com.mindhub.homebanking.utils.SendGridUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class KeyTokenServiceImpl implements KeyTokenService {

    @Autowired
    KeyTokenRepository keyTokenRepository;

    @Autowired
    SendGridUtil sendGridUtil;

    @Override
    public KeyToken generateAndSendKeyToken(String email, Client newClient) throws IOException {

        String token;

        KeyToken keyToken;

        do{

            token = UUID.randomUUID().toString();

        }while(keyTokenRepository.existsByToken(token));

        keyToken = new KeyToken(token, LocalDateTime.now().plusDays(1));

        sendGridUtil.sendEmailConfirmation(email, keyToken.getToken());

        return keyToken;


    }

    @Override
    public KeyToken findByToken(String token) {
        return keyTokenRepository.findByToken(token);
    }

    @Override
    public boolean existsByToken(String token) {

       return keyTokenRepository.existsByToken(token);

    }

}
