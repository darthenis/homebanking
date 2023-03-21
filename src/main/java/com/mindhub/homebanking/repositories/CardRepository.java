package com.mindhub.homebanking.repositories;

import com.mindhub.homebanking.models.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource
public interface CardRepository extends JpaRepository<Card, Long> {

    public Optional<Card> findByNumber(String number);

    public Boolean existsByNumber(String number);

}
