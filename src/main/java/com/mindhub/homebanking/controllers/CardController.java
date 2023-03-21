package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dto.CardDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.services.impl.CardServiceImpl;
import com.mindhub.homebanking.services.impl.ClientServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class CardController {

    @Autowired
    CardServiceImpl cardService;

    @Autowired
    ClientServiceImpl clientService;


    @GetMapping("/cards")
    public List<Card> getAllCards(){

        return cardService.getAllCards();

    }

    @GetMapping("/clients/current/cards")
    public List<CardDTO> getCards(Authentication authentication) {

        return cardService.getCurrentCards(authentication).stream().map(CardDTO::new).collect(Collectors.toList());

    }

    @PostMapping("/clients/current/cards")
    public ResponseEntity<?> createCard(@RequestParam CardType cardType,
                                        @RequestParam CardColor cardColor,
                                        @RequestParam(required = false) String accountNumber,
                                        Authentication authentication){

        try{

            System.out.println(cardColor);

            System.out.println(cardType);

            cardService.createCard(authentication, cardType, cardColor, accountNumber);

            return new ResponseEntity<>(HttpStatus.CREATED);

        }catch(Exception exception){

            return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);

        }


    }

    @DeleteMapping("/clients/current/cards/{id}")
    public ResponseEntity<?> deleteCard(@PathVariable Long id, Authentication authentication){

        Client client = clientService.findByEmail(authentication.getName());
        try{

            cardService.deleteCard(client, id);

            return new ResponseEntity<>("Delete successfully", HttpStatus.OK);

        }catch(EntityNotFoundException entityNotFoundException){

            return new ResponseEntity<>(entityNotFoundException.getMessage(), HttpStatus.FORBIDDEN);

        }

    }


}
