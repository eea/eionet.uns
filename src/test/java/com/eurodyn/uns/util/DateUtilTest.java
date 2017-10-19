package com.eurodyn.uns.util;

import com.eurodyn.uns.ApplicationTestContext;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class DateUtilTest {
    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    Date testDate;

    @Before
    public void setUp() throws Exception {
        testDate = dateFormat.parse("10-12-2013 15:22:33");
        assertDate(testDate, 10, 12, 2013);
        assertTime(testDate, 15, 22, 33);
    }

    @Test
    public void testStartOfADay_doesNotChangeDate() throws Exception {
        Date date = DateUtil.startOfADay(testDate);

        assertDate(date, 10, 12, 2013);
    }

    @Test
    public void testStartOfADay_truncatesTime() throws Exception {
        Date date = DateUtil.startOfADay(testDate);

        assertTime(date, 0, 0, 0);
    }

    @Test
    public void testSecondBeforeMidnight_doesNotChangeDate() throws Exception {
        Date date = DateUtil.secondBeforeMidnight(testDate);

        assertDate(date, 10, 12, 2013);
    }

    @Test
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
