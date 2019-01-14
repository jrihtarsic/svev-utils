/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.as4mail.svev.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.getInstance;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author vsrs
 */
public class XSLTExtensions {
    
    private static final ThreadLocal<DateFormat> S_DATE_FORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("dd. MM. yyyy");
        }
    };

    private static final ThreadLocal<DateFormat> S_DATE_TIME_FORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("dd. MM. yyyy HH:mm");
        }
    };

    /**
     * Method returs current date string representation
     *
     * @return current date
     */
    public static Object currentDate() {
        return S_DATE_FORMAT.get().format(getInstance().getTime());
    }

    /**
     * Method returs current dateTime string representation
     *
     * @return current dateTime
     */
    public static Object currentDateTime() {
        return S_DATE_TIME_FORMAT.get().format(getInstance().getTime());
    }

    /**
     * Parse date from string
     *
     * @param str - date string representation
     * @return
     */
    public static Object formatDate(String str) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }

        Date dt = JAXBDateAdapter.parseDateTime(str);
        return S_DATE_FORMAT.get().format(dt);
    }

    /**
     * Return fiction date for start date
     *
     * @param str
     * @return
     */
    public static Object getZPPFictionDate(String str) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }
        Date dt = JAXBDateAdapter.parseDateTime(str);
        Calendar c = new GregorianCalendar();
        c.setTime(dt);
        c.add(DAY_OF_MONTH, 15);
        return S_DATE_FORMAT.get().format(c.getTime());

    }

   
}
