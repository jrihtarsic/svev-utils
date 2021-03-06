/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package com.as4mail.svev.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Jože Rihtaršič
 */
public class JAXBDateAdapter {

  /**
   *
   * @param s
   * @return
   */
  public static Date parseDate(String s) {
    if (s != null && !s.trim().isEmpty()) {
      return DatatypeConverter.parseDate(s).getTime();
    }
    return null;
  }

  /**
   *
   * @param s
   * @return
   */
  public static Date parseDateTime(String s) {
    if (s != null && !s.trim().isEmpty()) {
      return DatatypeConverter.parseDateTime(s).getTime();
    }
    return null;
  }

  /**
   *
   * @param dt
   * @return
   */
  public static String printDate(Date dt) {
    if (dt != null) {
      Calendar cal = new GregorianCalendar();
      cal.setTime(dt);
      return DatatypeConverter.printDate(cal);
    }
    return null;
  }

  /**
   *
   * @param dt
   * @return
   */
  public static String printDateTime(Date dt) {
    if (dt != null) {
      Calendar cal = new GregorianCalendar();
      cal.setTime(dt);
      return DatatypeConverter.printDateTime(cal);
    }
    return null;
  }
}
