package com.chedaojunan.report.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class DatahubDeviceData extends FixedFrequencyIntegrationData {

    @JsonProperty("adCode")
    private String adCode; // 行政区编码
    @JsonProperty("townCode")
    private String townCode; // 乡镇街道编码

    public String getAdCode() {
        return adCode;
    }

    public void setAdCode(String adCode) {
        this.adCode = adCode;
    }

    public String getTownCode() {
        return townCode;
    }

    public void setTownCode(String townCode) {
        this.townCode = townCode;
    }

    public DatahubDeviceData(){}

    public DatahubDeviceData(FixedFrequencyIntegrationData fixedFrequency, String adCode, String townCode) {
        setDeviceImei(fixedFrequency.getDeviceImei());
        setDeviceId(fixedFrequency.getDeviceId());
        setLocalTime(fixedFrequency.getLocalTime());
        setTripId(fixedFrequency.getTripId());
        setServerTime(fixedFrequency.getServerTime());
        setLatitude(fixedFrequency.getLatitude());
        setLongitude(fixedFrequency.getLongitude());
        setAltitude(fixedFrequency.getAltitude());
        setDirection(fixedFrequency.getDirection());
        setGpsSpeed(fixedFrequency.getGpsSpeed());
        setFlagGpsLoss(fixedFrequency.getFlagGpsLoss());

        setRoadApiStatus(fixedFrequency.getRoadApiStatus());
        setCrosspoint(fixedFrequency.getCrosspoint());
        setRoadName(fixedFrequency.getRoadName());
        setRoadLevel(fixedFrequency.getRoadLevel());
        setMaxSpeed(fixedFrequency.getMaxSpeed());
        setIntersection(fixedFrequency.getIntersection());
        setIntersectionDistance(fixedFrequency.getIntersectionDistance());
        setTrafficRequestTimesamp(fixedFrequency.getTrafficRequestTimesamp());
        setTrafficRequestId(fixedFrequency.getTrafficRequestId());
        setTrafficApiStatus(fixedFrequency.getTrafficApiStatus());
        setCongestionInfo(fixedFrequency.getCongestionInfo());

        setAdCode(adCode);
        setTownCode(townCode);

        setCorrectedLatitude(fixedFrequency.getCorrectedLatitude());
        setCorrectedLongitude(fixedFrequency.getCorrectedLongitude());

    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (IOException e) {
            return null;
        }
    }

}