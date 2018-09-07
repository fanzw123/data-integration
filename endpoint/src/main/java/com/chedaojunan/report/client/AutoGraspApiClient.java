package com.chedaojunan.report.client;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chedaojunan.report.common.Constants;
import com.chedaojunan.report.model.AutoGraspRequest;
import com.chedaojunan.report.model.AutoGraspRequestParam;
import com.chedaojunan.report.model.AutoGraspResponse;
import com.chedaojunan.report.model.ExtensionParamEnum;
import com.chedaojunan.report.model.FixedFrequencyIntegrationData;
import com.chedaojunan.report.model.RectangleTrafficInfoRequest;
import com.chedaojunan.report.model.RectangleTrafficInfoResponse;
import com.chedaojunan.report.model.RoadInfo;
import com.chedaojunan.report.utils.EndpointConstants;
import com.chedaojunan.report.utils.PrepareAutoGraspRequest;
import com.chedaojunan.report.utils.ResponseUtils;

import okhttp3.HttpUrl;
import okhttp3.Request;

public class AutoGraspApiClient extends Client<AutoGraspResponse> {

  private static final Logger LOG = LoggerFactory.getLogger(AutoGraspApiClient.class);
  private static final String API_NAME = "AUTO_GRASP_API";

  private static final String SEMI_COLON = ";";

  private static AutoGraspApiClient instance = null;

  protected AutoGraspApiClient() {
    super();
  }

  public static synchronized AutoGraspApiClient getInstance() {
    LOG.info("Creating AutoGraspApiClient connection");
    return getInstance(instance, AutoGraspApiClient.class, API_NAME);
  }

  protected Request createRequest(AutoGraspRequest autoGraspRequest) {
    HttpUrl httpUrl = new HttpUrl.Builder()
        .scheme("http")
        .host(url)
        .addPathSegment(apiVersion)
        .addPathSegments(pathSegment)
        .addQueryParameter(AutoGraspRequestParam.KEY, autoGraspRequest.getKey())
        .addQueryParameter(AutoGraspRequestParam.CAR_ID, autoGraspRequest.getCarId())
        .addQueryParameter(AutoGraspRequestParam.LOCATIONS, autoGraspRequest.getLocations())
        .addQueryParameter(AutoGraspRequestParam.TIME, autoGraspRequest.getTime())
        .addQueryParameter(AutoGraspRequestParam.DIRECTION, autoGraspRequest.getDirection())
        .addQueryParameter(AutoGraspRequestParam.SPEED, autoGraspRequest.getSpeed())
        .addQueryParameter(AutoGraspRequestParam.EXTENSIONS, ExtensionParamEnum.BASE.toString())
        .build();

    Request request = new Request.Builder()
        .url(httpUrl)
        .build();
    return request;
  }

  public AutoGraspResponse getAutoGraspResponse(AutoGraspRequest autoGraspRequest) {
    String autoGraspResponseString = getClientResponseJson(createRequest(autoGraspRequest));
    return ResponseUtils.convertStringToAutoGraspResponse(autoGraspResponseString);
  }

  public List<FixedFrequencyIntegrationData> getTrafficInfoFromAutoGraspResponse(AutoGraspRequest autoGraspRequest) {

    AutoGraspResponse autoGraspResponse = getAutoGraspResponse(autoGraspRequest);

    List<String> autoGraspRequestGpsList = Arrays.asList(autoGraspRequest.getLocations().split(Constants.ESCAPE_PIPE));

    int dataCount = autoGraspResponse.getCount();
    if (CollectionUtils.isEmpty(autoGraspRequestGpsList) || CollectionUtils.isEmpty(autoGraspResponse.getRoadInfoList()) ||
        (autoGraspRequestGpsList.size() != autoGraspResponse.getRoadInfoList().size()) ||
        autoGraspRequestGpsList.size() != dataCount ||
        autoGraspResponse.getRoadInfoList().size() != dataCount) {
      LOG.debug("status = 0 from AutoGrasp or autoGrasp locations cannot be matched with roads in response");
      //status = 0 for autograsp API, no need to enrich data
      List<FixedFrequencyIntegrationData> integrationDataList;
      FixedFrequencyIntegrationData integrationData = new FixedFrequencyIntegrationData();
      integrationData.setDeviceId(autoGraspRequest.getCarId());
      int requestGpsCount = autoGraspRequestGpsList.size();
      integrationDataList = Collections.nCopies(requestGpsCount, integrationData);
      return integrationDataList;
    }

    String apiKey = autoGraspRequest.getKey();
    RectangleTrafficInfoClient rectangleTrafficInfoClient = RectangleTrafficInfoClient.getInstance();
    List<RoadInfo> roadInfoList = autoGraspResponse.getRoadInfoList();

    List<FixedFrequencyIntegrationData> integrationDataList =
        IntStream.range(1, Math.min(autoGraspRequestGpsList.size(), autoGraspResponse.getCount()))
            .mapToObj(index -> {
              String validGPS1 = ResponseUtils.getValidGPS(index - 1, autoGraspRequestGpsList, roadInfoList);
              String validGPS2 = ResponseUtils.getValidGPS(index, autoGraspRequestGpsList, roadInfoList);
              String trafficInfoRequestRectangle = String.join(SEMI_COLON, validGPS1, validGPS2);
              String requestTimestamp = String.valueOf(Instant.now().toEpochMilli());
              String requestId = UUID.randomUUID().toString();

              FixedFrequencyIntegrationData integrationData = new FixedFrequencyIntegrationData();
              integrationData.setDeviceId(autoGraspRequest.getCarId());
              ResponseUtils.enrichDataWithAutoGraspResponse(integrationData, index - 1, autoGraspRequestGpsList, roadInfoList, autoGraspResponse, requestTimestamp, requestId);

              RectangleTrafficInfoRequest trafficInfoRequest = new RectangleTrafficInfoRequest(apiKey, trafficInfoRequestRectangle, requestId, requestTimestamp, null);
              RectangleTrafficInfoResponse trafficInfoResponse = rectangleTrafficInfoClient.getTrafficInfoResponse(trafficInfoRequest);

              // start mod for null check by 2018.04.05
              if (null != trafficInfoResponse.getTrafficInfo()) {
                ResponseUtils.enrichDataWithTrafficInfoResponse(integrationData, trafficInfoResponse.getStatus(), trafficInfoResponse.getTrafficInfo().toString());
              } else {
                ResponseUtils.enrichDataWithTrafficInfoResponse(integrationData, trafficInfoResponse.getStatus(), "");
              }
              // end mod for null check by 2018.04.05
              return integrationData;
            }).collect(Collectors.toList());

    //replicate traffic info for the last GPS
    int requestGpsListSize = autoGraspRequestGpsList.size();

    FixedFrequencyIntegrationData integrationData = new FixedFrequencyIntegrationData();
    integrationData.setDeviceId(autoGraspRequest.getCarId());
    String requestTimestamp = String.valueOf(Instant.now().toEpochMilli());
    String requestId = UUID.randomUUID().toString();

    FixedFrequencyIntegrationData integrationDataCopy = integrationDataList.get(integrationDataList.size() - 1);
    ResponseUtils.enrichDataWithAutoGraspResponse(integrationData, requestGpsListSize - 1, autoGraspRequestGpsList, roadInfoList, autoGraspResponse, requestTimestamp, requestId);
    ResponseUtils.enrichDataWithTrafficInfoResponse(integrationData, integrationDataCopy.getTrafficApiStatus(), integrationDataCopy.getCongestionInfo());
    integrationDataList.add(integrationData);

    return integrationDataList;
  }

}