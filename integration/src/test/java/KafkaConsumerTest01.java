import com.chedaojunan.report.model.FixedFrequencyIntegrationData;
import com.chedaojunan.report.model.FrequencyGpsData.FrequencyGps;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.Serdes;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class KafkaConsumerTest01 {

  private static final String BOOTSTRAP_SERVERS = "47.95.10.165:9092,47.93.24.115:9092,39.106.170.188:9092";

  public static List<String> runConsumer(String inputTopic) throws Exception{
    final Consumer<String, String> consumer = createConsumer(inputTopic);
    while (true) {
      final ConsumerRecords<String, String> consumerRecords = consumer.poll(10);
      for (ConsumerRecord<String, String> record : consumerRecords) {
        if(record.value().contains("353848049564565")) {
            System.out.println(record.value());
        }
      }
    }
  }

  private static Consumer<String, String> createConsumer(String inputTopic) {
    final Properties props = new Properties();
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "protoc002");
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, Serdes.String().deserializer().getClass());
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, Serdes.String().deserializer().getClass());
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
    // Create the consumer using props.
    final Consumer<String, String> consumer = new KafkaConsumer<>(props);
    // Subscribe to the topic.
    consumer.subscribe(Collections.singletonList(inputTopic));
    return consumer;
  }

  public static void main(String... args) throws Exception {
    String inputTopic = "test001";
    runConsumer(inputTopic);

  }


  public static FixedFrequencyIntegrationData convertToFixedAccessDataPojo(String accessDataString) {
    if (StringUtils.isEmpty(accessDataString))
      return null;
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      FixedFrequencyIntegrationData accessData = objectMapper.readValue(accessDataString, FixedFrequencyIntegrationData.class);
      return accessData;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

}
