import com.chedaojunan.report.model.FixedFrequencyGpsData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class KafkaProducerTest001 {

  private static final Logger logger = LoggerFactory.getLogger(KafkaProducerTest001.class);
  private static final String BOOTSTRAP_SERVERS = "47.95.10.165:9092,47.93.24.115:9092,39.106.170.188:9092";

  private Producer producer;

  int n=1;

  public void runProducer(String inputTopic) {

    Properties configProperties = new Properties();
    configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    configProperties.put(ProducerConfig.BATCH_SIZE_CONFIG, 100);
    configProperties.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 30000);
    configProperties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
    configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
            Serdes.String().serializer().getClass());
    configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
            Serdes.String().serializer().getClass());

    producer = new KafkaProducer(configProperties);

    String serverTime = System.currentTimeMillis() + "";

    for (int j = 1; j <= 5; j++) {
      n++;
      FixedFrequencyGpsData gpsData = new FixedFrequencyGpsData();
      gpsData.setDeviceImei("01test000"+j);
      gpsData.setDeviceId("01test000"+j);
      gpsData.setLocalTime((1521478861000l+j)+"");
      gpsData.setTripId("01test000");
      gpsData.setServerTime(serverTime);
      gpsData.setLatitude(29.0000+0.0001*n);
      gpsData.setLongitude(121.0000+0.0001*n);
      gpsData.setAltitude(12.9999);
      gpsData.setDirection(111.4);
      gpsData.setGpsSpeed(77.1626205444336);
      try {
//        System.out.println(new ObjectMapper().writeValueAsString(gpsData));
        producer.send(new ProducerRecord<>(inputTopic, gpsData.getDeviceId(), new ObjectMapper().writeValueAsString(gpsData)));
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
    KafkaProducerTest001 producerTest = new KafkaProducerTest001();
    String inputTopic = "device_gps_test";
    try {
      int count =10;
      while(count>0){
        count--;
        long startTime = System.currentTimeMillis();
        for (int i=0;i<60;i++) {
          producerTest.runProducer(inputTopic);
          Thread.sleep(650);
        }
        long totalTime = (System.currentTimeMillis()-startTime)/1000;
        System.out.println("!!!!!!!!!!!!!!!totalTime=:"+totalTime);
        Thread.sleep(900);
      }
    } catch (Exception e) {
    }
    producerTest.close();
  }

}