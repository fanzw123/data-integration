package com.chedaojunan.report.client;

import com.chedaojunan.report.model.RectangleTrafficInfoRequest;
import com.chedaojunan.report.model.RectangleTrafficInfoResponse;
import com.chedaojunan.report.utils.EndpointConstants;
import com.chedaojunan.report.utils.EndpointUtils;
import com.chedaojunan.report.utils.UrlUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

public class RectangleTrafficInfoClientTest {

  private RectangleTrafficInfoClient rectangleTrafficInfoClient;
  private RectangleTrafficInfoRequest rectangleTrafficInfoRequest;

  @Before
  public void init() throws IOException {
    rectangleTrafficInfoClient = RectangleTrafficInfoClient.getInstance();
    String apiKey = EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_API_KEY);
    String rectangle = "116.351147,39.966309;116.357134,39.968727";
    String requestTime = Instant.now().toString();
    String requestId = UUID.randomUUID().toString();
    rectangleTrafficInfoRequest = new RectangleTrafficInfoRequest(apiKey, rectangle, requestId, requestTime, null);
  }

  @Test
  public void testGetTrafficInfoResponse() throws Exception {
    RectangleTrafficInfoResponse response =
        rectangleTrafficInfoClient.getTrafficInfoResponse(rectangleTrafficInfoRequest);
    Assert.assertNotNull(response);
    System.out.println(response.getTrafficInfo().getDescription());
  }
}