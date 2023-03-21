package com.mindhub.homebanking.repositories;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
public class TransactionRepositoryTest {

    @Autowired
    TransactionRepository transactionRepository;

    @Test
    public void existsTransactions(){

        List<Transaction> transactions = transactionRepository.findAll();

        assertThat(transactions, is(not(empty())));

    }

    @Test
    public void existsTransactionTypeDebit(){

        List<Transaction> transactions = transactionRepository.findAll();

        assertThat(transactions, hasItem(hasProperty("type", is(TransactionType.DEBIT))));

    }
}
