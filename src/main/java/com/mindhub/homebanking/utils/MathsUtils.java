package com.mindhub.homebanking.utils;

public class MathsUtils {

    public static int random(int min, int max){

        return (int)Math.floor(Math.random() * (max - min) + min);

    }

}
