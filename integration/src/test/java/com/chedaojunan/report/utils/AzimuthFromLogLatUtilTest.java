package com.chedaojunan.report.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class AzimuthFromLogLatUtilTest {

  AzimuthFromLogLatUtil A = null;
  AzimuthFromLogLatUtil B = null;
  AzimuthFromLogLatUtil azimuthFromLogLatUtil = new AzimuthFromLogLatUtil();
  @Before
  public void init() throws IOException {
    A = new AzimuthFromLogLatUtil(116.496167, 39.917066);
    B = new AzimuthFromLogLatUtil(116.496149, 39.917205);
  }

  @Test
  public void testGetAzimuth() throws Exception {
    double angle = azimuthFromLogLatUtil.getAzimuth(A, B);
    Assert.assertEquals((Double)354.30, (Double)angle);
  }

  @Test
  public void testGetAngle() throws Exception {
    double angle = azimuthFromLogLatUtil.getAngle(A, B);
    Assert.assertEquals((Double)354.32796318856543, (Double)angle);
  }

  @Test
  public void testGetDistance() throws Exception {
    double distance = azimuthFromLogLatUtil.getDistance(116.496167, 39.917066, 116.496149, 39.917205);
    Assert.assertEquals((Double)16.0, (Double)distance);
  }

}
