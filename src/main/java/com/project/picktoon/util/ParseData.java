package com.project.picktoon.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ParseData {
    public static Date parseDate(String dateStr, SimpleDateFormat format){
        try{
            Date date = format.parse(dateStr);
            return date;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}
