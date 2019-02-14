package com.chedaojunan.report.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.chedaojunan.report.common.Constants;
import com.chedaojunan.report.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class ResponseUtils {

  private static final Logger logger = LoggerFactory.getLogger(ResponseUtils.class);

  private static final String INVALID_CROSSPOINT = "0,0";
  private static final String INVALID_COORDINATECONVERT_RESPONSE_GPS = "0,0";

  public static String validateStringOrStringArray (JsonNode jsonNode) {
    String nodeValue = "";
    switch (jsonNode.getNodeType()) {
      case STRING:
        nodeValue = jsonNode.asText();
        break;
      case ARRAY:
        break;
      default:
        logger.error("this json node value can only be String or String[], but found %s", jsonNode.getNodeType());
    }
    return nodeValue;
  }

  public static String getValidGPS(int index, List<String> autoGraspRequestGpsList, List<RoadInfo> autoGraspResponseRoadInfoList) {
    RoadInfo roadInfo = autoGraspResponseRoadInfoList.get(index);
    String origGPS = autoGraspRequestGpsList.get(index);
    String crosspoint = roadInfo.getCrosspoint();
    if (crosspoint.equals(INVALID_CROSSPOINT))
      return origGPS;
    else
      return crosspoint;
  }

  public static AutoGraspResponse convertStringToAutoGraspResponse(String autoGraspResponseString) {
    AutoGraspResponse autoGraspResponse = new AutoGraspResponse();
    // start add for null check by 2019.01.13
    if (autoGraspResponse == null) {
      autoGraspResponse.setCount(0);
      return autoGraspResponse;
    }
    // end add for null check by 2019.01.13
    try {
      JsonNode autoGraspResponseNode = ObjectMapperUtils.getObjectMapper().readTree(autoGraspResponseString);
      if (autoGraspResponseNode == null) {
        autoGraspResponse.setCount(0);
        return autoGraspResponse;
      } else {
        int autoGraspStatus = autoGraspResponseNode.get(AutoGraspResponse.STATUS).asInt();
        String autoGraspInfoString = autoGraspResponseNode.get(AutoGraspResponse.INFO).asText();
        String autoGraspInfoCode = autoGraspResponseNode.get(AutoGraspResponse.INFO_CODE).asText();

        int autoGraspCount = 0;
        if (null != autoGraspResponseNode.get(AutoGraspResponse.COUNT)) {
          autoGraspCount = autoGraspResponseNode.get(AutoGraspResponse.COUNT).asInt();
        }
//        int autoGraspCount = autoGraspResponseNode.get(AutoGraspResponse.COUNT).asInt();
        ArrayNode roadInfoArrayNode = (ArrayNode) autoGraspResponseNode.get(AutoGraspResponse.ROADS);
        List<RoadInfo> roadInfoList = new ArrayList<>();
        if (null != roadInfoArrayNode) {
          roadInfoArrayNode
                  .forEach(roadInfoNode -> roadInfoList.add(convertJsonNodeToRoadInfo(roadInfoNode)));
        }

        autoGraspResponse.setCount(autoGraspCount);
        autoGraspResponse.setRoadInfoList(roadInfoList);
        autoGraspResponse.setInfo(autoGraspInfoString);
        autoGraspResponse.setInfoCode(autoGraspInfoCode);
        autoGraspResponse.setStatus(autoGraspStatus);
        return autoGraspResponse;
      }
    } catch (IOException e) {
      logger.warn("cannot get roadInfo string %s", e.getMessage());
      return null;
    }
  }

  public static RoadInfo convertJsonNodeToRoadInfo(JsonNode roadInfoNode) {
    if (roadInfoNode == null)
      return null;
    else {

      String crosspoint = roadInfoNode.get(RoadInfo.CROSS_POINT).asText();
      JsonNode roadNameNode = roadInfoNode.get(RoadInfo.ROAD_NAME);
      String roadName = ResponseUtils.validateStringOrStringArray(roadNameNode);
      int roadLevel = roadInfoNode.get(RoadInfo.ROAD_LEVEL).asInt();
      int maxSpeed = roadInfoNode.get(RoadInfo.MAX_SPEED).asInt();
      JsonNode intersectionNode = roadInfoNode.get(RoadInfo.INTERSECTION);
      String intersection = ResponseUtils.validateStringOrStringArray(intersectionNode);
      String intersectionDistance = roadInfoNode.get(RoadInfo.INTERSECTION_DISTANCE).asText();
      RoadInfo roadInfo = new RoadInfo();
      roadInfo.setCrosspoint(crosspoint);
      roadInfo.setRoadname(roadName);
      roadInfo.setRoadlevel(roadLevel);
      roadInfo.setMaxspeed(maxSpeed);
      roadInfo.setIntersection(intersection);
      roadInfo.setIntersectiondistance(intersectionDistance);
      return roadInfo;
    }

  }

  public static void enrichDataWithAutoGraspResponse(FixedFrequencyIntegrationData integrationData,
                                              int index, List<String> autoGraspRequestGpsList, List<RoadInfo> roadInfoList,
                                              AutoGraspResponse autoGraspResponse, String requestTimestamp, String requestId) {
    RoadInfo roadInfo = roadInfoList.get(index);
    String validGPS = getValidGPS(index, autoGraspRequestGpsList, roadInfoList);
    integrationData.setRoadApiStatus(autoGraspResponse.getStatus());
    integrationData.setCrosspoint(validGPS);
    integrationData.setRoadName(roadInfo.getRoadname().toString());
    integrationData.setMaxSpeed(roadInfo.getMaxspeed());
    integrationData.setRoadLevel(roadInfo.getRoadlevel());
    integrationData.setIntersection(roadInfo.getIntersection().toString());
    integrationData.setIntersectionDistance(roadInfo.getIntersectiondistance());
    integrationData.setTrafficRequestId(requestId);
    integrationData.setTrafficRequestTimesamp(requestTimestamp);
  }

  public static FixedFrequencyAccessGpsData enrichDataWithCoordinateConvertResponse(FixedFrequencyGpsData gpsData, String coordinateConvertResponseGps) {
    FixedFrequencyAccessGpsData accessGpsData = null;
    try {
      accessGpsData = new FixedFrequencyAccessGpsData(gpsData);
      accessGpsData.setCorrectedLongitude(Double.parseDouble(coordinateConvertResponseGps.split(Constants.COMMA)[0]));
      accessGpsData.setCorrectedLatitude(Double.parseDouble(coordinateConvertResponseGps.split(Constants.COMMA)[1]));
    } catch (Exception e) {
      logger.warn("parse gps string %s", e.getMessage());
    }
    return accessGpsData;

  }

  public static FixedFrequencyIntegrationData enrichDataWithTrafficInfoResponse(FixedFrequencyIntegrationData integrationData,
                                                                         int trafficInfoResponseStatus, String congestionInfo) {
    integrationData.setTrafficApiStatus(trafficInfoResponseStatus);
    integrationData.setCongestionInfo(congestionInfo);

    return integrationData;
  }

  public static RectangleTrafficInfoResponse convertToTrafficInfoResponse (String trafficInfoResponseString) {
    RectangleTrafficInfoResponse trafficInfoResponse = new RectangleTrafficInfoResponse();
    // start add for null check by 2019.01.13
    if (trafficInfoResponseString == null) {
      trafficInfoResponse.setTrafficInfo(null);
      trafficInfoResponse.setStatus(0);
      return trafficInfoResponse;
    }
    // end add for null check by 2019.01.13
    try {
      JsonNode trafficInfoResponseNode = ObjectMapperUtils.getObjectMapper().readTree(trafficInfoResponseString);
      if (trafficInfoResponseNode == null) {
        trafficInfoResponse.setTrafficInfo(null);
        trafficInfoResponse.setStatus(0);
        return trafficInfoResponse;
      } else {
        int trafficInfoStatus = trafficInfoResponseNode.get(GaoDeApiResponse.STATUS).asInt();
        String trafficInfoString = trafficInfoResponseNode.get(GaoDeApiResponse.INFO).asText();
        String trafficInfoCode = trafficInfoResponseNode.get(GaoDeApiResponse.INFO_CODE).asText();

        JsonNode trafficInfoNode = trafficInfoResponseNode.get(RectangleTrafficInfoResponse.TRAFFIC_INFO);
        // start mod for null check by 2018.04.05
        TrafficInfo trafficInfo = null;
        if (null != trafficInfoNode) {
          trafficInfo = new TrafficInfo();
        // end mod for null check by 2018.04.05
          String trafficDescription = trafficInfoNode.get(TrafficInfo.DESCRIPTION).asText();

          JsonNode evaluationNode = trafficInfoNode.get(TrafficInfo.EVALUATION);
          Evaluation evaluation = new Evaluation();
          String evaluationExpedite = evaluationNode.get(Evaluation.EXPEDITE).asText();
          String evaluationCongested = evaluationNode.get(Evaluation.CONGESTED).asText();
          String evaluationBlocked = evaluationNode.get(Evaluation.BLOCKED).asText();
          String evaluationUnknown = evaluationNode.get(Evaluation.UNKNOWN).asText();
          String evaluationDescription = evaluationNode.get(Evaluation.DESCRIPTION).asText();
          String evaluationStatus = evaluationNode.get(Evaluation.STATUS).asText();
          evaluation.setBlocked(evaluationBlocked);
          evaluation.setCongested(evaluationCongested);
          evaluation.setDescription(evaluationDescription);
          evaluation.setExpedite(evaluationExpedite);
          evaluation.setStatus(evaluationStatus);
          evaluation.setUnknown(evaluationUnknown);

          trafficInfo.setDescription(trafficDescription);
          trafficInfo.setEvaluation(evaluation);
        // start mod for null check by 2018.04.05
        }
        // end mod for null check by 2018.04.05

        trafficInfoResponse.setTrafficInfo(trafficInfo);
        trafficInfoResponse.setInfo(trafficInfoString);
        trafficInfoResponse.setInfoCode(trafficInfoCode);
        trafficInfoResponse.setStatus(trafficInfoStatus);

        return trafficInfoResponse;
      }
    } catch (IOException e){
      logger.warn("cannot get roadInfo string %s", e.getMessage());
      return null;
    }

  }

  public static CoordinateConvertResponse convertStringToCoordinateConvertResponse(String coordinateConvertResponseString) {
    CoordinateConvertResponse coordinateConvertResponse = new CoordinateConvertResponse();
    // start add for null check by 2019.01.13
    if (coordinateConvertResponseString == null) {
      coordinateConvertResponse.setLocations(INVALID_COORDINATECONVERT_RESPONSE_GPS);
      return coordinateConvertResponse;
    }
    // end add for null check by 2019.01.13
    try {
      JsonNode coordinateConvertResponseNode = ObjectMapperUtils.getObjectMapper().readTree(coordinateConvertResponseString);
      if (coordinateConvertResponseNode == null) {
        coordinateConvertResponse.setLocations(INVALID_COORDINATECONVERT_RESPONSE_GPS);
        return coordinateConvertResponse;
      } else {
        int coordinateConvertStatus = coordinateConvertResponseNode.get(CoordinateConvertResponse.STATUS).asInt();
        String coordinateConvertInfoString = coordinateConvertResponseNode.get(CoordinateConvertResponse.INFO).asText();
        String coordinateConvertInfoCode = coordinateConvertResponseNode.get(CoordinateConvertResponse.INFO_CODE).asText();
        String coordinateConvertLocations = coordinateConvertResponseNode.get(CoordinateConvertResponse.LOCATIONS).asText();

        coordinateConvertResponse.setInfo(coordinateConvertInfoString);
        coordinateConvertResponse.setInfoCode(coordinateConvertInfoCode);
        coordinateConvertResponse.setStatus(coordinateConvertStatus);
        coordinateConvertResponse.setLocations(coordinateConvertLocations);
        return coordinateConvertResponse;
      }
    } catch (IOException e) {
      logger.warn("cannot get coordinate convert string %s", e.getMessage());
      return null;
    }
  }

}