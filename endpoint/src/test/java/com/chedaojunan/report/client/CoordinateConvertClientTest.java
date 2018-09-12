package com.chedaojunan.report.client;

import com.chedaojunan.report.model.CoordinateConvertRequest;
import com.chedaojunan.report.model.CoordinateConvertResponse;
import com.chedaojunan.report.utils.EndpointConstants;
import com.chedaojunan.report.utils.EndpointUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class CoordinateConvertClientTest {

  private CoordinateConvertClient coordinateConvertClient;
  private CoordinateConvertRequest coordinateConvertRequest;
  private String apiKey;

  @Before
  public void init() throws IOException {
    coordinateConvertClient = CoordinateConvertClient.getInstance();
    apiKey = EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_API_KEY);
  }

  @Test
  public void testGetCoordinateConvertResponseValid() throws Exception {
    String locationsString = "116.53215789794922,39.84053421020508|116.53213500976562,39.840518951416016|116.53211975097656,39.84050369262695";
    coordinateConvertRequest = new CoordinateConvertRequest(apiKey, locationsString, null);
    CoordinateConvertResponse response = coordinateConvertClient.getCoordinateConvertResponse(coordinateConvertRequest);
    Assert.assertNotNull(response);
    System.out.println(response.toString());
  }

}