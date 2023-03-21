package com.mindhub.homebanking.utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
public class MathsUtilsTest {

    @Test
    public void randomTest(){

        int number = MathsUtils.random(100, 1);

        assertThat(number, is(lessThan(101)));
        assertThat(number, is(greaterThan(0)));

    }

}
