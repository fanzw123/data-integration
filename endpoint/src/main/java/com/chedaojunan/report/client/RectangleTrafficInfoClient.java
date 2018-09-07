package com.chedaojunan.report.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chedaojunan.report.model.RectangleTrafficInfoRequest;
import com.chedaojunan.report.model.RectangleTrafficInfoResponse;
import com.chedaojunan.report.utils.ResponseUtils;

import okhttp3.HttpUrl;
import okhttp3.Request;

public class RectangleTrafficInfoClient extends Client<RectangleTrafficInfoResponse> {

  private static final Logger logger = LoggerFactory.getLogger(RectangleTrafficInfoClient.class);
  private static final String API_NAME = "RECTANGLE_TRAFFIC_INFO_API";

  private static RectangleTrafficInfoClient instance = null;

  protected RectangleTrafficInfoClient() {
    super();
  }

  public static synchronized RectangleTrafficInfoClient getInstance() {
    logger.info("Creating RectangleTrafficInfoApiClient connection");
    return getInstance(instance, RectangleTrafficInfoClient.class, API_NAME);
  }

  protected Request createRequest(RectangleTrafficInfoRequest rectangleTrafficInfoRequest) {
    HttpUrl httpUrl = new HttpUrl.Builder()
        .scheme("http")
        .host(url)
        .addPathSegment(apiVersion)
        .addPathSegments(pathSegment)
        .addQueryParameter(RectangleTrafficInfoRequest.KEY, rectangleTrafficInfoRequest.getKey())
        .addQueryParameter(RectangleTrafficInfoRequest.RECTANGLE, rectangleTrafficInfoRequest.getRectangle())
        .addQueryParameter(RectangleTrafficInfoRequest.EXTENSIONS, rectangleTrafficInfoRequest.getExtensions().toString())
        .build();

    Request request = new Request.Builder()
        .url(httpUrl)
        .build();
    return request;
  }

  public RectangleTrafficInfoResponse getTrafficInfoResponse(RectangleTrafficInfoRequest rectangleTrafficInfoRequest) {
    String trafficInfoResponseString = getClientResponseJson(createRequest(rectangleTrafficInfoRequest));
    return ResponseUtils.convertToTrafficInfoResponse(trafficInfoResponseString);
  }

}