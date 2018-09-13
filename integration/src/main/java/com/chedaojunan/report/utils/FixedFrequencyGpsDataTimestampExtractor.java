package com.chedaojunan.report.utils;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

public class FixedFrequencyGpsDataTimestampExtractor implements TimestampExtractor {

    @Override
    public long extract(ConsumerRecord<Object, Object> record, long previousTimestamp) {
        String rawDataString = record.value().toString();
//        FixedFrequencyGpsData rawData = SampledDataCleanAndRet.convertToFixedGpsDataPojo(rawDataString);
        if (rawDataString != null) {
            long milliseconds = Long.parseLong(rawDataString.split("\\n")[4].replace("\"","").split(":")[1].trim());
            return milliseconds;
        } else
            return -1L;
    }
}
