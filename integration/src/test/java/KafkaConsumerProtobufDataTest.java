import com.cdja.cloud.data.proto.GpsProto;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.Serdes;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class KafkaConsumerProtobufDataTest {

  private static final String BOOTSTRAP_SERVERS = "123.56.223.119:9092,123.56.216.151:9092,47.94.98.137:9092";
  private static final String INPUT_TOPIC = "deviceGpsProtoTest";

  public static List<String> runConsumer(String inputTopic) throws Exception{
    final Consumer<String, GpsProto> consumer = createConsumer(inputTopic);
    while (true) {
      final ConsumerRecords<String, GpsProto> consumerRecords = consumer.poll(10);
      for (ConsumerRecord<String, GpsProto> record : consumerRecords) {
        System.out.println(record.value());
      }
    }
  }

  private static Consumer<String, GpsProto> createConsumer(String inputTopic) {
    final Properties props = new Properties();
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "protoc001");
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        Serdes.String().deserializer().getClass());
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            com.chedaojunan.report.utils.ProtoDeserializer.class);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
    // Create the consumer using props.
    final Consumer<String, GpsProto> consumer = new KafkaConsumer<>(props);
    // Subscribe to the topic.
    consumer.subscribe(Collections.singletonList(inputTopic));
    return consumer;
  }

  public static void main(String... args) throws Exception {
    runConsumer(INPUT_TOPIC);
  }

}
