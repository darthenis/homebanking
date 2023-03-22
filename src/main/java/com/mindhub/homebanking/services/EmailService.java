package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.EmailDetails;

public interface EmailService {

    String sendSimpleMail(EmailDetails details);

    String sendMailToken(String email, String token);
}
