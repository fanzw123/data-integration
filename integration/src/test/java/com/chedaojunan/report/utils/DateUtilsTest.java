package com.chedaojunan.report.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class DateUtilsTest {

    DateUtils dateUtils = new DateUtils();
    long localTime = 0L;
    long utcTime = 0L;
    long serverTime = 0L;

    @Before
    public void init() throws IOException {
        localTime = 1521446239000L;
        utcTime = 1521417439000L;
        serverTime = 1521446240000L;

    }

//    @Test
//    public void testGetYMD() throws Exception {
//        Assert.assertEquals("20180320", dateUtils.getYMD());
//    }
//
//    @Test
//    public void testGetHM() throws Exception {
//        Assert.assertEquals("17_52", dateUtils.getHM());
//    }

    @Test
    public void testGetUTCTimeFromLocal() throws Exception {
        Assert.assertEquals(1521417439L, dateUtils.getUTCTimeFromLocal(localTime));
    }

    @Test
    public void testGetLocalTimeFromUTC() throws Exception {
        Assert.assertEquals(1521446239000L, dateUtils.getLocalTimeFromUTC(utcTime));
    }

    @Test
    public void testGetYMDFromTime() throws Exception {
        Assert.assertEquals("20180319", dateUtils.getYMDFromTime(serverTime));
    }

    @Test
    public void testGetHMFromTime() throws Exception {
        Assert.assertEquals("15_57", dateUtils.getHMFromTime(serverTime));
    }

    @Test
    public void testGetHourFromTime() throws Exception {
        Assert.assertEquals("15", dateUtils.getHourFromTime(serverTime));
    }

    @Test
    public void testGetMinuteFromTime() throws Exception {
        Assert.assertEquals("57", dateUtils.getMinuteFromTime(serverTime));
    }

}