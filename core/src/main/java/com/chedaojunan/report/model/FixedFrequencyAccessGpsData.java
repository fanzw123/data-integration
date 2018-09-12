package com.chedaojunan.report.model;

public class FixedFrequencyAccessGpsData extends FixedFrequencyGpsData {

  private double correctedLatitude				; // 修正后纬度
  private double correctedLongitude			    ; // 修正后经度

  public FixedFrequencyAccessGpsData(){}

  public FixedFrequencyAccessGpsData(FixedFrequencyGpsData accessData, double correctedLatitude, double correctedLongitude) {
    setDeviceId(accessData.getDeviceId());
    setDeviceImei(accessData.getDeviceImei());
    setLocalTime(accessData.getLocalTime());
    setServerTime(accessData.getServerTime());
    setTripId(accessData.getTripId());
    setLatitude(accessData.getLatitude());
    setLongitude(accessData.getLongitude());
    setAltitude(accessData.getAltitude());
    setGpsSpeed(accessData.getGpsSpeed());
    setDirection(accessData.getDirection());

    setCorrectedLatitude(correctedLatitude);
    setCorrectedLongitude(correctedLongitude);

  }

  public FixedFrequencyAccessGpsData(FixedFrequencyGpsData accessData) {
    setDeviceId(accessData.getDeviceId());
    setDeviceImei(accessData.getDeviceImei());
    setLocalTime(accessData.getLocalTime());
    setServerTime(accessData.getServerTime());
    setTripId(accessData.getTripId());
    setLatitude(accessData.getLatitude());
    setLongitude(accessData.getLongitude());
    setAltitude(accessData.getAltitude());
    setGpsSpeed(accessData.getGpsSpeed());
    setDirection(accessData.getDirection());
  }

  public double getCorrectedLatitude() {
    return correctedLatitude;
  }

  public void setCorrectedLatitude(double correctedLatitude) {
    this.correctedLatitude = correctedLatitude;
  }

  public double getCorrectedLongitude() {
    return correctedLongitude;
  }

  public void setCorrectedLongitude(double correctedLongitude) {
    this.correctedLongitude = correctedLongitude;
  }

}
