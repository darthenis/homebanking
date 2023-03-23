package com.mindhub.homebanking.repositories;

import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.ClientLoan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
public class ClientLoanRepositoryTest {

    //@Autowired
    //ClientLoanRepository clientLoanRepository;

  /*  @Test
    public void existsClientLoan(){

        List<ClientLoan> clientLoans = clientLoanRepository.findAll();

        assertThat(clientLoans, is(not(empty())));

    }

    @Test
    public void existsTypeString(){

        List<ClientLoan> clientLoans = clientLoanRepository.findAll();

        assertThat(clientLoans, hasItem(hasProperty("amount", isA(Double.class))));

    }*/

}
