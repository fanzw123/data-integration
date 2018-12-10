package com.chedaojunan.report.utils;

import com.cdja.cloud.data.proto.GpsProto;
import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fanzw
 * @description: protoc工厂类
 */
public class ProtoFactory {

  private static final Logger logger = LoggerFactory.getLogger(ProtoFactory.class);

  public static GpsProto.Gps createProtoClass(GpsProto.Gps gpsData) {

    GpsProto.Gps.Builder builder = GpsProto.Gps.newBuilder()
            .setDeviceId(gpsData.getDeviceId())
            .setDeviceImei(gpsData.getDeviceImei())
            .setLocalTime(gpsData.getLocalTime())
            .setServerTime(gpsData.getServerTime())
            .setTripId(gpsData.getTripId())
            .setLat(gpsData.getLat())
            .setLongi(gpsData.getLongi())
            .setAlt(gpsData.getAlt())
            .setDirection(gpsData.getDirection())
            .setGpsSpeed(gpsData.getGpsSpeed())
            .setFlagGpsLoss(gpsData.getFlagGpsLoss());

    return builder.build();
  }

  public static GpsProto.Gps getFrequencyGps(byte[] bytes) {
    try {
      return GpsProto.Gps.parseFrom(bytes);
    } catch (InvalidProtocolBufferException e) {
      logger.error("getFrequencyGps gpsProto parse error!!!");
    }
    return null;
  }

}