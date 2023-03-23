package com.mindhub.homebanking.repositories;

import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.repositories.CardRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
public class cardReporsitoryTest {

    //@Autowired
    //CardRepository cardRepository;

   /* @Test
    public void existsCards(){

        List<Card> cards = cardRepository.findAll();

        assertThat(cards, is(not(empty())));

    }

    @Test
    public void existsDebitCard(){

        List<Card> cards = cardRepository.findAll();

        assertThat(cards, hasItem(hasProperty("cardType", is(CardType.DEBIT))));
    }*/
}
