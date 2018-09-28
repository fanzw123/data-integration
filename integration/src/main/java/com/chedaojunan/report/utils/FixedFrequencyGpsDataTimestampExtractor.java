package com.chedaojunan.report.utils;

import com.cdja.cloud.data.proto.GpsProto;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

public class FixedFrequencyGpsDataTimestampExtractor implements TimestampExtractor {

    @Override
    public long extract(ConsumerRecord<Object, Object> record, long previousTimestamp) {

        GpsProto.Gps gps = (GpsProto.Gps) record.value();
        if (gps != null && gps.getServerTime() != null) {
            long milliseconds = Long.parseLong(gps.getServerTime());
            return milliseconds;
        } else
            return -1L;
    }
}
