package com.mindhub.homebanking.services.impl;

import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.KeyToken;
import com.mindhub.homebanking.repositories.KeyTokenRepository;
import com.mindhub.homebanking.services.KeyTokenService;
import com.mindhub.homebanking.utils.SendGridUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class KeyTokenServiceImpl implements KeyTokenService {

    @Autowired
    KeyTokenRepository keyTokenRepository;

    @Value("${sendgrid.apikey}")
    private String sendGridApiKey;

    @Value("${sendgrid.email}")
    private String myEmail;


    @Override
    public KeyToken generateAndSendKeyToken(String email, Client newClient) throws Exception {

        String token;

        KeyToken keyToken;

        do{

            token = UUID.randomUUID().toString();

        }while(keyTokenRepository.existsByToken(token));

        keyToken = new KeyToken(token, LocalDateTime.now().plusDays(1));

        new SendGridUtil().sendEmailConfirmation(email, keyToken.getToken(), sendGridApiKey, myEmail);

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
