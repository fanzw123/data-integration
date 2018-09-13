package com.chedaojunan.report.utils;

import com.chedaojunan.report.model.FrequencyGpsData;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;

import java.io.IOException;
import java.util.Properties;

public class ProtoTest {

    private static final String BOOTSTRAP_SERVERS = "47.95.10.165:9092,47.93.24.115:9092,39.106.170.188:9092";

    private static Producer producer;
    public static void main(String[] args) throws IOException {

        // 按照定义的数据结构，创建一个Person
        FrequencyGpsData.FrequencyGps.Builder gpsBuilder = FrequencyGpsData.FrequencyGps.newBuilder();

        for (int i = 0; i < 100; i++) {

            gpsBuilder.setDeviceImei("test000002" + i);
            gpsBuilder.setDeviceId("test0000002" + i);
            gpsBuilder.setTripId("111100" + i);
            gpsBuilder.setLocalTime("1521478861001");
            gpsBuilder.setServerTime("1521478861111");
            gpsBuilder.setLat(39.990475);
            gpsBuilder.setLongi(116.481499);
            gpsBuilder.setAlt(30.98);
            gpsBuilder.setDir(98.00);
            gpsBuilder.setGpsSpeed(98.00);

//        GpsData.Gps gps = ProtoFactory.createProtoClass(gpsBuilder.build());
            // -------------- 分割线：上面是发送方，将数据序列化后发送 ---------------
            String inputTopic = "device_gps_test";
            runProducer(inputTopic, gpsBuilder.build());
        }

        // -------------- 分割线：下面是接收方，将数据接收后反序列化 ---------------
        // 接收到流并读取，如网络输入流，这里用ByteArrayInputStream来代替
        // 反序列化
//        FrequencyGpsData.FrequencyGps xxg2 = FrequencyGpsData.FrequencyGps.parseFrom(input);
//        System.out.println("DeviceId:" + xxg2.getDeviceId());
//        System.out.println("DeviceImei:" + xxg2.getDeviceImei());
    }

    public static void runProducer(String inputTopic, FrequencyGpsData.FrequencyGps gpsBuilder) {

        Properties configProperties = new Properties();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                Serdes.String().serializer().getClass());
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                com.chedaojunan.report.utils.ProtoSerializer.class);

        producer = new KafkaProducer(configProperties);
        try {
//            System.out.println(gpsBuilder);
            producer.send(new ProducerRecord<String, FrequencyGpsData.FrequencyGps>(inputTopic, gpsBuilder));
        } catch (Exception ex) {
            ex.printStackTrace();//handle exception here
        }
//    }
    }
}