package com.mindhub.homebanking.repositories;

import com.mindhub.homebanking.models.KeyToken;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class KeyTokenRepositoryTest {

    //@Autowired
    //KeyTokenRepository keyTokenRepository;

    /*@Test
    public void createKeyToken(){

        KeyToken keyToken = new KeyToken("token", LocalDateTime.now());

        keyTokenRepository.save(keyToken);

        List<KeyToken> keyTokens = keyTokenRepository.findAll();

        assertThat(keyTokens, is(not(empty())));

    }

    @Test
    public void deleteKeyToken(){

        List<KeyToken> keyToken = keyTokenRepository.findAll();

        assertThat(keyToken, is(empty()));

    }*/

}
