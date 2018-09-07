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
    String locationsString = "116.496167,39.917066|116.496149,39.917205|116.496149,39.917326";
    coordinateConvertRequest = new CoordinateConvertRequest(apiKey, locationsString, null);
    CoordinateConvertResponse response = coordinateConvertClient.getCoordinateConvertResponse(coordinateConvertRequest);
    Assert.assertNotNull(response);
    System.out.println(response.toString());
  }

}