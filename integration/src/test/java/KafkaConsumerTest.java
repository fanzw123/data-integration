import com.chedaojunan.report.model.FrequencyGpsData.FrequencyGps;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.Serdes;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class KafkaConsumerTest {

  private static final String BOOTSTRAP_SERVERS = "47.95.10.165:9092,47.93.24.115:9092,39.106.170.188:9092";

  public static List<String> runConsumer(String inputTopic) throws Exception{
    final Consumer<String, FrequencyGps> consumer = createConsumer(inputTopic);
    while (true) {
      final ConsumerRecords<String, FrequencyGps> consumerRecords = consumer.poll(10);
      for (ConsumerRecord<String, FrequencyGps> record : consumerRecords) {
          record.value();
        System.out.println(record.value());
      }
    }
  }

  private static Consumer<String, FrequencyGps> createConsumer(String inputTopic) {
    final Properties props = new Properties();
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "protoc001");
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        Serdes.String().deserializer().getClass());
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            com.chedaojunan.report.utils.ProtoDeserializer.class);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
    // Create the consumer using props.
    final Consumer<String, FrequencyGps> consumer = new KafkaConsumer<>(props);
    // Subscribe to the topic.
    consumer.subscribe(Collections.singletonList(inputTopic));
    return consumer;
  }

  public static void main(String... args) throws Exception {
    String inputTopic = "test004";
    runConsumer(inputTopic);

  }

}
