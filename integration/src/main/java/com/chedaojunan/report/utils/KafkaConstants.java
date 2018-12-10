package com.chedaojunan.report.utils;

public class KafkaConstants {

  public static final String PROPERTIES_FILE_NAME = "kafkastream.properties";

  // Kafka brokers
  public static final String RAWDATA_STREAM_APPLICATION_NAME = "rawdata.stream.application.name";
  public static final String WRITE_DATAHUB_STREAM_APPLICATION_NAME = "write.datahub.stream.application.name";
  public static final String KAFKA_RAW_DATA_TOPIC = "kafka.raw.data.topic";
  public static final String KAFKA_OUTPUT_TOPIC = "kafka.output.topic";
  public static final String KAFKA_BOOTSTRAP_SERVERS = "bootstrap.servers";
  public static final String KAFKA_WINDOW_DURATION = "kafka.window.duration.seconds"; //seconds
  public static final String AUTO_OFFSET_RESET_CONFIG = "kafka.auto.offset.rest";

  public static final String MAX_REQUEST_SIZE_CONFIG = "max.request.size.config";
  public static final String RAWDATA_ENRICHED_REQUEST_TIMEOUT_MS_CONFIG = "rawdata.enriched.request.timeout.ms.config";
  public static final String WRITE_DATAHUB_REQUEST_TIMEOUT_MS_CONFIG = "write.datahub.request.timeout.ms.config";
  public static final String WRITE_DATAHUB_COMMIT_INTERVAL_MS_CONFIG = "write.datahub.commit.interval.ms.config";

  public static final String HYPHEN = "-";

  public static final String COORDINATE_CONVERT_LENGTH = "coordinate.convert.length";

  public static final String RAWDATA_STATE_DIR_CONFIG = "rawData.state.dir.config";
  public static final String WRITE_DATAHUB_STATE_DIR_CONFIG = "write.datahub.state.dir.config";
  public static final String DEDUP_STORE_NAME = "dedup.store.name";

}
