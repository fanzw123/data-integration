import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Stream;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaProducerTest {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaProducerTest.class);
  //private static final String BOOTSTRAP_SERVERS = "47.95.10.165:9092,47.93.24.115:9092,39.106.170.188:9092";
  private static final String BOOTSTRAP_SERVERS = "localhost:9092";

  private Producer producer;

  public void runProducer(String dataFile, String inputTopic) {

    Properties configProperties = new Properties();
    configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
        Serdes.String().serializer().getClass());
    configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        Serdes.String().serializer().getClass());


    producer = new KafkaProducer(configProperties);
    String testDataFile = dataFile;
    try {
      Path path = Paths.get(getClass().getClassLoader()
          .getResource(testDataFile).toURI());
      Stream<String> rawDataStream = Files.lines(path);
      rawDataStream.forEach(message -> {
        producer.send(new ProducerRecord<String, String>(inputTopic, message));
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      });
    } catch (Exception ex) {
      ex.printStackTrace();//handle exception here
    }
  }

  public void close() {
    if (producer != null) {
      producer.close();
      LOG.info("Kafka producer is closed.");
    }
  }

  public static void main(String[] args) {
    KafkaProducerTest producerTest = new KafkaProducerTest();
    String dataFile = "testdata";
    String inputTopic = "hy-raw-data-test";
    producerTest.runProducer(dataFile, inputTopic);
    producerTest.close();
  }
}
