import com.cdja.cloud.data.proto.GpsProto;
import com.chedaojunan.report.utils.ProtoSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class KafkaProducerPerformanceTest {

  private static final Logger logger = LoggerFactory.getLogger(KafkaProducerPerformanceTest.class);
  private static final String BOOTSTRAP_SERVERS = "47.95.10.165:9092,47.93.24.115:9092,39.106.170.188:9092";
  private static final String INPUT_TOPIC = "deviceGpsProtoTest";

  private Producer producer;

  int n = 1;

  public void runProducer(String inputTopic, int i) {

    Properties configProperties = new Properties();
    configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    configProperties.put(ProducerConfig.BATCH_SIZE_CONFIG, 100);
    configProperties.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 30000);
    configProperties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
    configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
            Serdes.String().serializer().getClass());
    configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
            ProtoSerializer.class);

    producer = new KafkaProducer(configProperties);

    String serverTime = System.currentTimeMillis()+"";

    for (int j = 0; j < 50; j++) {
      n++;
      GpsProto.Gps.Builder gpsData = GpsProto.Gps.newBuilder();
      gpsData.setDeviceId("04test0000" + j);
      gpsData.setDeviceImei("04test0000" + j);
//      if (i % 2 == 0)
//        gpsData.setLocalTime("1521478861000" + i);
//      else
//        gpsData.setLocalTime("15214788610000");
      gpsData.setLocalTime(serverTime);
      System.out.println("04test000" + j+" ; serverTime=: "+ serverTime);
      gpsData.setServerTime(serverTime);
      gpsData.setTripId("04test000");
      gpsData.setLat(31.90791893005371 + 0.0001 * n);
      gpsData.setLongi(118.70845794677734 + 0.0001 * n);
      gpsData.setAlt(12.9999);
      gpsData.setDirection(111.4);
      gpsData.setGpsSpeed(77.1626205444336);
      gpsData.setFlagGpsLoss(0);

      try {
        producer.send(new ProducerRecord<>(inputTopic, gpsData.getDeviceId(), gpsData.build()));
      } catch (Exception ex) {
        ex.printStackTrace();//handle exception here
      }
    }
  }

  public void close() {
    if (producer != null) {
      producer.close();
      logger.info("Kafka producer is closed.");
    }
  }

  public static void main(String[] args) {
    KafkaProducerPerformanceTest producerTest = new KafkaProducerPerformanceTest();
    try {
      for(int j=0;j<10;j++) {
        for (int i = 1; i <= 60; i++) {
          producerTest.runProducer(INPUT_TOPIC, i);
          Thread.sleep(770);
        }
      }
    } catch (Exception e) {
    }
    producerTest.close();
  }

}