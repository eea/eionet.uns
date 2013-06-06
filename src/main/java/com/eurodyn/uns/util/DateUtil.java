/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 * 
 * The Original Code is Unified Notification System
 * 
 * The Initial Owner of the Original Code is European Environment
 * Agency (EEA).  Portions created by European Dynamics (ED) company are
 * Copyright (C) by European Environment Agency.  All Rights Reserved.
 * 
 * Contributors(s):
 *    Original code: Nedeljko Pavlovic (ED) 
*/


package com.eurodyn.uns.util;

import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.Date;



/**
 *  Description of the Class
 *
 * @author     nedeljko
 */
public class DateUtil {

   private static String messagePattern1 = "yyyy-MM-dd HH:mm";
   private static String messagePattern2 = "yyyyMMdd";


   /**
    *  Description of the Method
    *
    * @param  date                Description of the Parameter
    * @return                     Description of the Return Value
    * @exception  ParseException  Description of the Exception
    */
   public static String messageFormatWithTime(Date date) throws ParseException {
      SimpleDateFormat newFormat = new SimpleDateFormat(messagePattern1);
      return newFormat.format(date);
   }

   public static Date dateFormatWithTime(String date) throws ParseException {
          SimpleDateFormat newFormat = new SimpleDateFormat(messagePattern1);
          return newFormat.parse(date);
   }

   /**
    *  Description of the Method
    *
    * @param  date                Description of the Parameter
    * @return                     Description of the Return Value
    * @exception  ParseException  Description of the Exception
    */
   public static String messageFormatNoTime(Date date) throws ParseException {
      SimpleDateFormat newFormat = new SimpleDateFormat(messagePattern2);
      return newFormat.format(date);
   }
   
   public static Date dateFormatNoTime(String date) throws ParseException {
          SimpleDateFormat newFormat = new SimpleDateFormat(messagePattern2);
          return newFormat.parse(date);
   }
   
   /**
    *  Pending. Remains condition for end of year.
    *
    * @param  date  Description of the Parameter
    * @return       Description of the Return Value
    */
   public static Date incrementByDays(Date date, int numDays) {
      Calendar calendar = new GregorianCalendar();
      calendar.setTime(date);
      calendar.add(Calendar.DAY_OF_YEAR, numDays);
      return calendar.getTime();
   }
   
   public static long getCurrentUTC() {
       Calendar c = Calendar.getInstance();
       c.setTime(new Date());
       c.setTimeZone(TimeZone.getTimeZone("UTC"));
       
       int day=c.get(Calendar.DAY_OF_MONTH);
       int month=c.get(Calendar.MONTH);
       int year=c.get(Calendar.YEAR);
       int hour=c.get(Calendar.HOUR_OF_DAY);
       int min=c.get(Calendar.MINUTE);
       int sec=c.get(Calendar.SECOND);
       
       Calendar trt = new GregorianCalendar();
       trt.set(Calendar.DAY_OF_MONTH, day);           
       trt.set(Calendar.MONTH, month);
       trt.set(Calendar.YEAR, year);
       trt.set(Calendar.HOUR_OF_DAY, hour);
       trt.set(Calendar.MINUTE, min);
       trt.set(Calendar.SECOND, sec);
       
       return trt.getTimeInMillis();
   }
   
   
   public static Date getCurrentUTCDate() {
       Calendar c = Calendar.getInstance();
       c.setTime(new Date());
       c.setTimeZone(TimeZone.getTimeZone("UTC"));
       
       int day=c.get(Calendar.DAY_OF_MONTH);
       int month=c.get(Calendar.MONTH);
       int year=c.get(Calendar.YEAR);
       int hour=c.get(Calendar.HOUR_OF_DAY);
       int min=c.get(Calendar.MINUTE);
       int sec=c.get(Calendar.SECOND);
       
       Calendar trt = new GregorianCalendar();
       trt.set(Calendar.DAY_OF_MONTH, day);           
       trt.set(Calendar.MONTH, month);
       trt.set(Calendar.YEAR, year);
       trt.set(Calendar.HOUR_OF_DAY, hour);
       trt.set(Calendar.MINUTE, min);
       trt.set(Calendar.SECOND, sec);
       
       return trt.getTime();
   }
   

   public static Timestamp toTimeStamp(Date date) {
      return new Timestamp(date.getTime());
   }

   
   public static long getUTCTime(Date date) {
       Calendar c = Calendar.getInstance();
       c.setTime(date);
       c.setTimeZone(TimeZone.getTimeZone("UTC"));
       c.getTime();
       
       int day=c.get(Calendar.DAY_OF_MONTH);
       int month=c.get(Calendar.MONTH);
       int year=c.get(Calendar.YEAR);
       int hour=c.get(Calendar.HOUR_OF_DAY);
       int min=c.get(Calendar.MINUTE);
       int sec=c.get(Calendar.SECOND);
       
       Calendar trt = new GregorianCalendar();
       trt.set(Calendar.DAY_OF_MONTH, day);           
       trt.set(Calendar.MONTH, month);
       trt.set(Calendar.YEAR, year);
       trt.set(Calendar.HOUR_OF_DAY, hour);
       trt.set(Calendar.MINUTE, min);
       trt.set(Calendar.SECOND, sec);
       
       return trt.getTimeInMillis();
   }
   
   
}