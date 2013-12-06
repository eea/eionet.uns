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
 * The Original Code is Web Questionnaires 2
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency. Portions created by TripleDev are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 *        Anton Dmitrijev
 */
package com.eurodyn.uns.util;

import junit.framework.TestCase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtilTest extends TestCase {
    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    Date testDate;

    @Override
    public void setUp() throws Exception {
        testDate = dateFormat.parse("10-12-2013 15:22:33");
        assertDate(testDate, 10, 12, 2013);
        assertTime(testDate, 15, 22, 33);
    }

    public void testStartOfADay_doesNotChangeDate() throws Exception {
        Date date = DateUtil.startOfADay(testDate);

        assertDate(date, 10, 12, 2013);
    }

    public void testStartOfADay_truncatesTime() throws Exception {
        Date date = DateUtil.startOfADay(testDate);

        assertTime(date, 0, 0, 0);
    }

    public void testSecondBeforeMidnight_doesNotChangeDate() throws Exception {
        Date date = DateUtil.secondBeforeMidnight(testDate);

        assertDate(date, 10, 12, 2013);
    }

    public void testSecondBeforeMidnight_setTimeToOneSecondBeforeMidnight() throws Exception {
        Date date = DateUtil.secondBeforeMidnight(testDate);

        assertTime(date, 23, 59, 59);
    }

    private void assertDate(Date actual, int expectedDay, int expectedMonth, int expectedYear) {
        Calendar calendar = createFromDate(actual);
        assertEquals(expectedDay, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(expectedMonth - 1, calendar.get(Calendar.MONTH));
        assertEquals(expectedYear, calendar.get(Calendar.YEAR));
    }

    private void assertTime(Date actual, int expectedHour, int expectedMinute, int expectedSecond) {
        Calendar calendar = createFromDate(actual);
        assertEquals(expectedHour, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(expectedMinute, calendar.get(Calendar.MINUTE));
        assertEquals(expectedSecond, calendar.get(Calendar.SECOND));
    }


    private Calendar createFromDate(Date source) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(source);
        return calendar;
    }
}
