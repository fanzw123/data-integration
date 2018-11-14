package com.chedaojunan.report.utils;

public class KafkaConstants {

  public static final String PROPERTIES_FILE_NAME = "kafkastream.properties";

  // Kafka brokers
  public static final String KAFKA_STREAM_APPLICATION_NAME = "data.enrich.with.traffic.info";
  public static final String KAFKA_RAW_DATA_TOPIC = "kafka.raw.data.topic";
  public static final String KAFKA_OUTPUT_TOPIC = "kafka.output.topic";
  public static final String KAFKA_BOOTSTRAP_SERVERS = "bootstrap.servers";
  public static final String KAFKA_GROUP_ID = "kafka.group.id";
  public static final String KAFKA_WINDOW_DURATION = "kafka.window.duration.seconds"; //seconds
  public static final String AUTO_OFFSET_RESET_CONFIG = "kafka.auto.offset.rest";

  public static final String HYPHEN = "-";

  public static final String COORDINATE_CONVERT_LENGTH = "coordinate.convert.length";

}
