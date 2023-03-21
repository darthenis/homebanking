package com.mindhub.homebanking.repositories;

import com.mindhub.homebanking.models.KeyToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeyTokenRepository extends JpaRepository<KeyToken, Long> {

    public KeyToken findByToken(String token);

    public boolean existsByToken(String token);

}
