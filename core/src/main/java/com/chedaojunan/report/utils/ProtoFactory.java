package com.chedaojunan.report.utils;

import com.chedaojunan.report.model.GpsData;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @author fanzw
 * @description: protoc工厂类
 */
public class ProtoFactory {
  public static GpsData.Gps createProtoClass(GpsData.Gps gpsData) {
    GpsData.Gps.Builder builder = GpsData.Gps.newBuilder()
            .setDeviceId(gpsData.getDeviceId())
            .setDeviceImei(gpsData.getDeviceImei())
            .setLocalTime(gpsData.getLocalTime())
            .setTripId(gpsData.getTripId())
            .setServerTime(gpsData.getServerTime())
            .setLatitude(gpsData.getLatitude())
            .setLongitude(gpsData.getLongitude())
            .setAltitude(gpsData.getAltitude())
            .setDirection(gpsData.getDirection())
            .setGpsSpeed(gpsData.getGpsSpeed());

    return builder.build();
  }

  public static GpsData.Gps getFrequencyGps(byte[] bytes) {
    try {
      return GpsData.Gps.parseFrom(bytes);
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
    return null;
  }

}