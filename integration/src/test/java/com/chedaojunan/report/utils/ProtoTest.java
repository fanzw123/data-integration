package com.chedaojunan.report.utils;

import com.chedaojunan.report.model.GpsData;
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
        GpsData.Gps.Builder gpsBuilder = GpsData.Gps.newBuilder();
        gpsBuilder.setDeviceImei("test000002");
        gpsBuilder.setDeviceId("test000002");
        gpsBuilder.setTripId("111112");
        gpsBuilder.setLocalTime("1521478861001");
        gpsBuilder.setServerTime("55555589");
        gpsBuilder.setLatitude(33339.990475);
        gpsBuilder.setLongitude(116.481499);
        gpsBuilder.setAltitude(30.98);
        gpsBuilder.setDirection(98.00);
        gpsBuilder.setGpsSpeed(98.00);

        GpsData.Gps gps = ProtoFactory.createProtoClass(gpsBuilder.build());
        // -------------- 分割线：上面是发送方，将数据序列化后发送 ---------------
        String inputTopic = "test004";
        runProducer(inputTopic, gps);

        // -------------- 分割线：下面是接收方，将数据接收后反序列化 ---------------
        // 接收到流并读取，如网络输入流，这里用ByteArrayInputStream来代替
        // 反序列化
//        FrequencyGpsData.FrequencyGps xxg2 = FrequencyGpsData.FrequencyGps.parseFrom(input);
//        System.out.println("DeviceId:" + xxg2.getDeviceId());
//        System.out.println("DeviceImei:" + xxg2.getDeviceImei());
    }

    public static void runProducer(String inputTopic, GpsData.Gps gpsBuilder) {

        Properties configProperties = new Properties();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                Serdes.String().serializer().getClass());
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                com.chedaojunan.report.utils.ProtoSerializer.class);

        producer = new KafkaProducer(configProperties);
        try {
//            System.out.println(gpsBuilder);
            producer.send(new ProducerRecord<String, GpsData.Gps>(inputTopic, gpsBuilder));
        } catch (Exception ex) {
            ex.printStackTrace();//handle exception here
        }
//    }
    }
}