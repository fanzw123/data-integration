package com.chedaojunan.report.client;

import com.chedaojunan.report.model.AutoGraspRequest;
import com.chedaojunan.report.model.AutoGraspRequestParam;
import com.chedaojunan.report.model.AutoGraspResponse;
import com.chedaojunan.report.model.FixedFrequencyIntegrationData;
import com.chedaojunan.report.utils.EndpointConstants;
import com.chedaojunan.report.utils.EndpointUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

public class AutoGraspApiClientTest {

  private AutoGraspApiClient autoGraspApiClient;
  private AutoGraspRequest autoGraspRequest;
  private String apiKey;
  private String carId;


  @Before
  public void init() throws IOException {
    autoGraspApiClient = AutoGraspApiClient.getInstance();
    apiKey = EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_API_KEY);
    carId = "abcd123456";
  }

  @Test
  public void testGetAutoGraspResponseValid() throws Exception {

//                             116.53804060861069,39.8416602942181|116.53801779197377,39.84164509345781|116.53800258027111,39.841629873043274
    String locationString =   "116.53803873698,39.841658799914|116.538015950521,39.841643880209|116.538000759549,39.841628417969";
    String timeString =       "1536635356,1536635357,1536635358";
    String speedString =      "6.950603008270264,8.794337272644043,8.93331241607666";
    String directionString =  "225.8,226.1,224.8";

//    String locationString =   "116.496167,39.917066|116.496149,39.917205|116.496149,39.917326";
//    String timeString =       "1434077500,1434077501,1434077510";
//    String speedString =      "1.0,1.0,2.0";
//    String directionString =  "1.0,1.0,2.0";
    autoGraspRequest = new AutoGraspRequest(apiKey, carId, locationString, timeString, directionString, speedString);
    AutoGraspResponse response = autoGraspApiClient.getAutoGraspResponse(autoGraspRequest);
    Assert.assertNotNull(response);
    Assert.assertEquals(3, response.getCount());
    System.out.println(response.toString());
  }

  @Test
  public void testGetAutoGraspResponseInValid() throws Exception {
    String locationString = "116.496167,39.917066|116.496159,39.917326|116.496169,39.917326";
    String timeString = "1489239756,1489239758,1489239759";
    String speedString = "4.0,6.0,7.0";
    String directionString = "358.6,90.0,90.0";
    autoGraspRequest = new AutoGraspRequest(apiKey, carId, locationString, timeString, directionString, speedString);
    AutoGraspResponse response = autoGraspApiClient.getAutoGraspResponse(autoGraspRequest);
    Assert.assertNotNull(response);
    Assert.assertEquals(3, response.getCount());
    System.out.println(response.toString());
  }

  @Test
  public void testGetTrafficInfoFromAutoGraspResponse() {
    String locationString = "116.496167,39.917066|116.496149,39.917205|116.496149,39.917326";
    String timeString = "1434077500,1434077501,1434077510";
    String speedString = "1.0,1.0,2.0";
    String directionString = "1.0,1.0,2.0";
    autoGraspRequest = new AutoGraspRequest(apiKey, carId, locationString, timeString, directionString, speedString);
    List<FixedFrequencyIntegrationData> gaodeApiResponseList = autoGraspApiClient.getTrafficInfoFromAutoGraspResponse(autoGraspRequest);
    Assert.assertEquals(3, gaodeApiResponseList.size());
  }

  @Test
  public void testGetTrafficInfoFromAutoGraspResponseInvalid () {
    String locationString = "116.496167,39.917066|116.496159,39.917326|116.496169,39.917326";
    String timeString = "1489239756,1489239758,1489239759";
    String speedString = "4.0,6.0,7.0";
    String directionString = "358.6,90.0,90.0";
    autoGraspRequest = new AutoGraspRequest(apiKey, carId, locationString, timeString, directionString, speedString);
    List<FixedFrequencyIntegrationData> gaodeApiResponseList = autoGraspApiClient.getTrafficInfoFromAutoGraspResponse(autoGraspRequest);
    Assert.assertEquals(3, gaodeApiResponseList.size());
    gaodeApiResponseList.stream().forEach(integrationData -> System.out.println(integrationData.toString()));
  }

  @Test
  public void testGetTrafficInfoFromAutoGraspResponseXN () {
    ExternalApiExecutorService.getExecutorService().submit(new Runnable() {
      @Override
      public void run() {
        String locationString = "116.496167,39.917066|116.496159,39.917326|116.496169,39.917326";
        String timeString = "1489239756,1489239758,1489239759";
        String speedString = "4.0,6.0,7.0";
        String directionString = "358.6,90.0,90.0";
        autoGraspRequest = new AutoGraspRequest(apiKey, carId, locationString, timeString, directionString, speedString);
        List<FixedFrequencyIntegrationData> gaodeApiResponseList = autoGraspApiClient.getTrafficInfoFromAutoGraspResponse(autoGraspRequest);
        Assert.assertEquals(3, gaodeApiResponseList.size());
        gaodeApiResponseList.stream().forEach(integrationData -> System.out.println(integrationData.toString()));
      }
    });
  }
}