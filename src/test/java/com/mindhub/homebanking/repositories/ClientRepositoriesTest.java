package com.mindhub.homebanking.repositories;

import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;

@SpringBootTest
public class ClientRepositoriesTest {

    @Autowired
    ClientRepository clientRepository;

    @Test
    public void existsClients(){

        List<Client> clients = clientRepository.findAll();

        assertThat(clients, is(not(empty())));

    }

    @Test
    public void exitsPerson(){

        List<Client> clients = clientRepository.findAll();

        assertThat(clients, hasItem(hasProperty("firstName", is("Melba"))));

    }

}
