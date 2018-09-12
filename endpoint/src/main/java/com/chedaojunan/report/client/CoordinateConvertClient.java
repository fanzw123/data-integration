package com.chedaojunan.report.client;

import com.chedaojunan.report.common.Constants;
import com.chedaojunan.report.model.*;
import com.chedaojunan.report.utils.ResponseUtils;
import okhttp3.HttpUrl;
import okhttp3.Request;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CoordinateConvertClient extends Client<CoordinateConvertResponse> {

  private static final Logger LOG = LoggerFactory.getLogger(CoordinateConvertClient.class);
  private static final String API_NAME = "COORDINATE_CONVERT_API";

  private static CoordinateConvertClient instance = null;

  protected CoordinateConvertClient() {
    super();
  }

  public static synchronized CoordinateConvertClient getInstance() {
    LOG.info("Creating CoordinateConvertClient connection");
    return getInstance(instance, CoordinateConvertClient.class, API_NAME);
  }

  protected Request createRequest(CoordinateConvertRequest coordinateConvertRequest) {
    HttpUrl httpUrl = new HttpUrl.Builder()
        .scheme("http")
        .host(url)
        .addPathSegment(apiVersion)
        .addPathSegments(pathSegment)
        .addQueryParameter(CoordinateConvertRequestParam.KEY, coordinateConvertRequest.getKey())
        .addQueryParameter(CoordinateConvertRequestParam.LOCATIONS, coordinateConvertRequest.getLocations())
        .addQueryParameter(CoordinateConvertRequestParam.COORDSYS, coordinateConvertRequest.getCoordsys().toString())
        .build();

    Request request = new Request.Builder()
        .url(httpUrl)
        .build();
    return request;
  }

  public CoordinateConvertResponse getCoordinateConvertResponse(CoordinateConvertRequest coordinateConvertRequest) {
    String coordinateConvertResponseString = getClientResponseJson(createRequest(coordinateConvertRequest));
    return ResponseUtils.convertStringToCoordinateConvertResponse(coordinateConvertResponseString);
  }

  public List<FixedFrequencyAccessGpsData> getCoordinateConvertFromResponse(List<FixedFrequencyGpsData> gpsDataListNew, CoordinateConvertRequest coordinateConvertRequest) {

    List<FixedFrequencyAccessGpsData> accessDataList = new ArrayList<>();
    CoordinateConvertResponse coordinateConvertResponse = getCoordinateConvertResponse(coordinateConvertRequest);

    List<String> coordinateConvertResponseGpsList = Arrays.asList(coordinateConvertResponse.getLocations().split(Constants.SEMICOLON));

    FixedFrequencyGpsData gpsData;
    for (int i = 0; i < gpsDataListNew.size(); i++) {
      gpsData = gpsDataListNew.get(i);
      if (!CollectionUtils.isEmpty(coordinateConvertResponseGpsList) && !StringUtils.isEmpty(coordinateConvertResponseGpsList.get(i))) {
        FixedFrequencyAccessGpsData frequencyAccessGpsData = ResponseUtils.enrichDataWithCoordinateConvertResponse(gpsData, coordinateConvertResponseGpsList.get(i));
        accessDataList.add(frequencyAccessGpsData);
      }
    }

    return accessDataList;
  }

}