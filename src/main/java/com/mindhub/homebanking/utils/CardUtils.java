package com.mindhub.homebanking.utils;

public class CardUtils {


    public static String generateCardNumber(){

        return  String.format("%04d", MathsUtils.random(9999, 1)) + " "
                + String.format("%04d", MathsUtils.random(9999, 1)) + " "
                + String.format("%04d", MathsUtils.random(9999, 1)) + " "
                + String.format("%04d", MathsUtils.random(9999, 1));

    }
}
