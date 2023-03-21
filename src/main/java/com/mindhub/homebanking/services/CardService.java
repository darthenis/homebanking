package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

public interface CardService {

        List<Card> getAllCards();

        List<Card> getCurrentCards(Authentication authentication);

        void createCard(Authentication authentication, CardType cardType, CardColor cardColor, String accountNumber) throws EntityNotFoundException, IllegalArgumentException;

        void deleteCard(Client client, Long id) throws EntityNotFoundException;

        Optional<Card> findByNumber(String number);

        String generateNumberCard();
}
