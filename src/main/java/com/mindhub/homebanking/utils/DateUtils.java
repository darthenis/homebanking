package com.mindhub.homebanking.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {


    public DateUtils(){}

    static public String formatDate(String preDate) throws ParseException {

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = inputFormat.parse(preDate);

        return outputFormat.format(date);
    }

}
