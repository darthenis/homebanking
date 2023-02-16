package com.mindhub.homebanking.dto;

import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;

import java.time.LocalDate;

public class CardDTO {

    private Long id;

    private CardType cardType;

    private CardColor cardColor;

    private String cardHolder;

    private String number;

    private String cvv;

    private LocalDate fromDate;

    private LocalDate thruDate;

    public CardDTO(Card card){

        id = card.getId();
        cardType = card.getCardType();
        cardColor = card.getCardColor();
        cardHolder = card.getCardHolder();
        number = card.getNumber();
        cvv = card.getCvv();
        fromDate = card.getFromDate();
        thruDate = card.getThruDate();

    }

    public Long getId() {
        return id;
    }

    public CardType getCardType() {
        return cardType;
    }

    public CardColor getCardColor() {
        return cardColor;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public String getNumber() {
        return number;
    }

    public String getCvv() {
        return cvv;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public LocalDate getThruDate() {
        return thruDate;
    }
}
