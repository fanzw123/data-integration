package com.chedaojunan.report.utils;

import com.chedaojunan.report.model.FixedFrequencyGpsData;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

public class FixedFrequencyGpsDataTimestampExtractor implements TimestampExtractor {

    @Override
    public long extract(ConsumerRecord<Object, Object> record, long previousTimestamp) {
        String rawDataString = record.value().toString();
        FixedFrequencyGpsData rawData = SampledDataCleanAndRet.convertToFixedGpsDataPojo(rawDataString);
        if (rawData != null) {
            long milliseconds = Long.parseLong(rawData.getServerTime());
            return milliseconds;
        } else
            return -1L;
    }
}
