import com.chedaojunan.report.model.FixedFrequencyAccessData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.stream.IntStream;

public class KafkaProducerTest005 {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaProducerTest005.class);
  private static final String BOOTSTRAP_SERVERS = "47.95.10.165:9092,47.93.24.115:9092,39.106.170.188:9092";
  //private static final String BOOTSTRAP_SERVERS = "127.0.0.1:9092";

  private Producer producer;

  public void runProducer(String inputTopic, int i) {

    Properties configProperties = new Properties();
    configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    configProperties.put(ProducerConfig.BATCH_SIZE_CONFIG, 0);
    configProperties.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 30000);
    configProperties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
    configProperties.put(ProducerConfig.RETRIES_CONFIG, 3);
    configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
        Serdes.String().serializer().getClass());
    configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        Serdes.String().serializer().getClass());

    producer = new KafkaProducer(configProperties);

    String serverTime = System.currentTimeMillis() + "";

    for (int j = 0; j < 10; j++) {
      FixedFrequencyAccessData accessData;
      accessData = new FixedFrequencyAccessData();
      accessData.setDeviceId("TEST000000" + j +"");
      accessData.setDeviceImei("test000000");
      accessData.setTripId("8c09580045634d72a9d2912d0a8c6c9b");
      if (i % 2 == 0)
        accessData.setLocalTime("1521478861000" + i);
      else
        accessData.setLocalTime("15214788610000");
      accessData.setServerTime(serverTime);
      accessData.setLatitude(29.999921798706055+0.00001*j);
      accessData.setLongitude(121.2059555053711+0.00001*j);
      accessData.setAltitude(12.899999618530273);
      accessData.setDirection(111.4);
      accessData.setGpsSpeed(77.1626205444336);
      accessData.setYawRate(0.007675438653677702);
      accessData.setAccelerateZ(-0.8040000200271606);
      accessData.setRollRate(-0.01864035055041313);
      accessData.setAccelerateX(-0.32499998807907104);
      accessData.setPitchRate(-0.017543859779834747);
      accessData.setAccelerateY(-8.581000328063965);
      accessData.setSourceId("001");
      try {
        System.out.println(new ObjectMapper().writeValueAsString(accessData));
        producer.send(new ProducerRecord<>(inputTopic, accessData.getDeviceId(), new ObjectMapper().writeValueAsString(accessData)));
      } catch (Exception ex) {
        ex.printStackTrace();//handle exception here
      }
    }
  }

  public void close() {
    if (producer != null) {
      producer.close();
      LOG.info("Kafka producer is closed.");
    }
  }

  public static void main(String[] args) {
    KafkaProducerTest005 producerTest = new KafkaProducerTest005();
    String inputTopic = "deviceGpsTest";
    try {
//      int i=0;
//      while(true){
//        i++;
      for(int i=1;i<=10;i++) {
        producerTest.runProducer(inputTopic,i);
      }
//        Thread.sleep(910);
//      }
    } catch (Exception e) {
    }
    producerTest.close();
  }
}