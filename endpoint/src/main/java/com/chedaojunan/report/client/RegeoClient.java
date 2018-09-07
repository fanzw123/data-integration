package com.chedaojunan.report.client;

import com.chedaojunan.report.model.*;
import com.chedaojunan.report.utils.EndpointConstants;
import com.chedaojunan.report.utils.EndpointUtils;
import com.chedaojunan.report.utils.Pair;
import com.chedaojunan.report.utils.PrepareCoordinateConvertRequest;
import okhttp3.HttpUrl;
import okhttp3.Request;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RegeoClient extends Client<RegeoResponse>{

  private static final Logger logger = LoggerFactory.getLogger(RegeoClient.class);
  private static final String API_NAME = "REGEO_API";
  private static RegeoClient instance = null;

  private static ObjectMapper objectMapper;

  static {
    objectMapper = new ObjectMapper();
  }

  protected RegeoClient() {
    super();
  }

  public static synchronized RegeoClient getInstance() {
    logger.info("Creating Regeo connection");
    return getInstance(instance, RegeoClient.class, API_NAME);
  }

  protected Request createRequest(RegeoRequest regeoRequest) {
    HttpUrl httpUrl = new HttpUrl.Builder()
            .scheme("http")
            .host(url)
            .addPathSegment(apiVersion)
            .addPathSegments(pathSegment)
            .addQueryParameter(RegeoRequest.KEY, regeoRequest.getKey())
            .addQueryParameter(RegeoRequest.LOCATION, regeoRequest.getLocation())
            .addQueryParameter(RegeoRequest.EXTENSIONS, regeoRequest.getExtensions())
            .addQueryParameter(RegeoRequest.BATCH, regeoRequest.getBatch())
            .addQueryParameter(RegeoRequest.ROADLEVEL, regeoRequest.getRoadlevel())
            .build();

    Request request = new Request.Builder()
            .url(httpUrl)
            .build();
    return request;
  }

  // regeo request parameter
  public static RegeoRequest regeoRequestParm(List<FixedFrequencyIntegrationData> fixedFrequencyDataList) {
    FixedFrequencyIntegrationData fixedFrequency;
    Pair<Double, Double> location;
    List<Pair<Double, Double>> locations = new ArrayList<>();

    if (!CollectionUtils.isEmpty(fixedFrequencyDataList) && ObjectUtils.allNotNull(fixedFrequencyDataList.get(0))) {
      fixedFrequency = fixedFrequencyDataList.get(0);
      location = new Pair<>(fixedFrequency.getLongitude(), fixedFrequency.getLatitude());
      locations.add(location);

      String locationsString = PrepareCoordinateConvertRequest.convertLocationsToRequestString(locations);
      String apiKey = EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_API_KEY);

      RegeoRequest regeoRequest = new RegeoRequest(apiKey, locationsString, null, null, null);
      return regeoRequest;
    } else {
      return null;
    }
  }

  public ArrayList<DatahubDeviceData> getRegeoFromResponse(List<FixedFrequencyIntegrationData> fixedFrequencyList) {
    DatahubDeviceData datahubDeviceData;
    ArrayList<DatahubDeviceData> datahubDeviceDataList = new ArrayList<>();

    RegeoRequest regeoRequest = regeoRequestParm(fixedFrequencyList);

    RegeoResponse regeoResponse = getRegeoResponse(regeoRequest);

    List<Regeocodes> regeoResponseList=null;
    if (regeoResponse != null) {
      regeoResponseList = regeoResponse.getRegeocodes();
    }

    FixedFrequencyIntegrationData fixedFrequencyIntegrationData;
    for (int i = 0; i < fixedFrequencyList.size(); i++) {
      fixedFrequencyIntegrationData = fixedFrequencyList.get(i);
      if (ObjectUtils.allNotNull(fixedFrequencyIntegrationData) && null!=regeoResponseList) {
        datahubDeviceData = enrichDataWithRegeoResponse(fixedFrequencyIntegrationData, regeoResponseList.get(0));
      } else {
        datahubDeviceData = enrichDataWithRegeoResponse(fixedFrequencyIntegrationData, null);
      }
      if (ObjectUtils.allNotNull(datahubDeviceData)) {
        datahubDeviceDataList.add(datahubDeviceData);
      }
    }

    return datahubDeviceDataList;
  }

  public static DatahubDeviceData enrichDataWithRegeoResponse(FixedFrequencyIntegrationData fixedFrequency, Regeocodes regeocodes) {
    DatahubDeviceData datahubDeviceData;
    try {
      if (null!=regeocodes) {
        datahubDeviceData = new DatahubDeviceData(fixedFrequency, regeocodes.getAddressComponent().getAdcode(), regeocodes.getAddressComponent().getTowncode());
      } else {
        datahubDeviceData = new DatahubDeviceData(fixedFrequency, null, null);
      }

    } catch (Exception e) {
      logger.warn("parse regeo string %s", e.getMessage());
      return null;
    }
    return datahubDeviceData;
  }

  public RegeoResponse getRegeoResponse(RegeoRequest regeoRequest) {
    String regeoResponseString = getClientResponseJson(createRequest(regeoRequest));
    return convertToRegeo(regeoResponseString);
  }

  public static RegeoResponse convertToRegeo(String convertToRegeoResponseString) {
    RegeoResponse regeoResponse = new RegeoResponse();
    try {
      if (StringUtils.isEmpty(convertToRegeoResponseString)) {
        return null;
      }
      JsonNode convertToRegeoResponseNode = objectMapper.readTree(convertToRegeoResponseString);
      if (convertToRegeoResponseNode == null)
        return null;
      else {
        regeoResponse.setInfo(ObjectUtils.allNotNull(convertToRegeoResponseNode.get(RegeoResponse.INFO))
                ?convertToRegeoResponseNode.get(RegeoResponse.INFO).asText():"");
        regeoResponse.setInfoCode(ObjectUtils.allNotNull(convertToRegeoResponseNode.get(RegeoResponse.INFO_CODE))
                ?convertToRegeoResponseNode.get(RegeoResponse.INFO_CODE).asText():"");
        regeoResponse.setStatus(ObjectUtils.allNotNull(convertToRegeoResponseNode.get(RegeoResponse.STATUS))
                ?convertToRegeoResponseNode.get(RegeoResponse.STATUS).asInt():0);

        JsonNode convertToRegeoRegeocodes = convertToRegeoResponseNode.get(RegeoResponse.REGEOCODES);
        if (null!=convertToRegeoRegeocodes) {
          regeoResponse.setRegeocodes(parserArray(convertToRegeoRegeocodes));
        }
        return regeoResponse;
      }
    } catch (IOException e) {
      logger.warn("cannot get regeo string %s", e.getMessage());
      return null;
    }
  }

  private static List<Regeocodes> parserArray(JsonNode jsonNode) {
    if (null!=jsonNode && !jsonNode.isArray()) {
      throw new RuntimeException("json对象不是数组类型");
    }
    List<Regeocodes> result = new ArrayList<>();
    for (JsonNode node : jsonNode) {
      result.add(parserSingle(node));
    }
    return result;
  }

  private static Regeocodes parserSingle(JsonNode node) {
    Regeocodes regeocodes = new Regeocodes();
    JsonNode addressJsonNode = node.get("addressComponent");

    AddressComponent addressComponent = new AddressComponent();
    String adcode = addressJsonNode.get("adcode").asText();
    String towncode = addressJsonNode.get("towncode").asText();

    addressComponent.setAdcode(adcode);
    addressComponent.setTowncode(towncode);

    regeocodes.setAddressComponent(addressComponent);
    return regeocodes;
  }

}