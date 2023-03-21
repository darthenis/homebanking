package com.mindhub.homebanking.services.impl;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.utils.CardUtils;
import com.mindhub.homebanking.utils.MathsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CardServiceImpl implements CardService {

    @Autowired
    ClientServiceImpl clientService;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    AccountServiceImpl accountService;

    @Override
    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }

    @Override
    public List<Card> getCurrentCards(Authentication authentication) {

        Client client = clientService.getCurrentClient(authentication);

        return client.getCards().stream().filter(card -> !card.getIsDisabled()).collect(Collectors.toList());

    }

    @Override
    public void createCard(Authentication authentication, CardType cardType, CardColor cardColor, String accountNumber) throws EntityNotFoundException, IllegalArgumentException, AccessDeniedException {

        if(cardType == null){

            throw new IllegalArgumentException("missing cardType param");

        } else if(cardColor == null){

            throw new IllegalArgumentException("missing cardColor param");

        }

        Client client = clientService.findByEmail(authentication.getName());

        if(client.getAddress() == null && cardType.equals(CardType.CREDIT)) throw new AccessDeniedException("You need add a address to create a new credit card");

        if(client.getCountry() == null && cardType.equals(CardType.CREDIT)) throw new AccessDeniedException("You need add a country to create a new credit card");

        if(client.getTel() == null && cardType.equals(CardType.CREDIT)) throw new AccessDeniedException("You need add a telephone to create a new credit card");

        if(accountNumber == null && cardType.equals(CardType.DEBIT)) throw new AccessDeniedException("Miss account number");

        Set<Card> cardsOfType = client.getCards().stream().filter(card -> card.getCardType() == cardType).collect(Collectors.toSet());

        if(cardsOfType.stream().filter(card -> !card.getIsDisabled()).collect(Collectors.toSet()).size() == 3){

            throw new AccessDeniedException("The client already have 3 cards of type "+ cardType);

        }

        String numberCard = this.generateNumberCard();

        String randomCvv = String.format("%03d", (int)Math.floor(Math.random() * (999 + 1)));

        Card newCard = new Card(cardType, cardColor, client.getFirstName() + " " + client.getLastName(), numberCard, randomCvv, LocalDate.now(), LocalDate.now().plusYears(5));

        if(cardType.equals(CardType.DEBIT)){

            Optional<Account> account = accountService.findByNumber(accountNumber);

            if(account.isEmpty()) throw new EntityNotFoundException("Account not found");

            newCard.setAccount(account.get());

        }

        client.addCard(newCard);

        clientService.save(client);

        cardRepository.save(newCard);

    }

    public String generateNumberCard(){

        String number;

        do{

            number = CardUtils.generateCardNumber();

        }while(cardRepository.existsByNumber(number));

        return number;

    }

    @Override
    public void deleteCard(Client client, Long id) throws EntityNotFoundException {

        Card clientCard = client.getCards().stream().filter(card -> card.getId().equals(id)).findAny().orElse(null);

        if(clientCard == null) {

            throw new AccessDeniedException("Card not found");

        }

        clientCard.setIsDisabled(true);

        cardRepository.save(clientCard);

    }

    @Override
    public Optional<Card> findByNumber(String number) {
        return cardRepository.findByNumber(number);
    }
}
