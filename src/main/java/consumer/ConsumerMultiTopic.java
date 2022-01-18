package consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ConsumerMultiTopic {

    private static final Logger LOG = LoggerFactory.getLogger(ConsumerMultiTopic.class);


    public static void main(String[] args) {


        String use_case_id = "fsi-1";

        Properties properties = new Properties();
        String brokerURI = "localhost:9092";
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerURI);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, use_case_id);
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, use_case_id);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");



        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Arrays.asList("iot", "clusterA.iot","clusterB.iot"));


        do {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> eventrecord : records)
                LOG.info("Received " +
                        "topic: " + eventrecord.topic() + ", " +
                        "partition: " + eventrecord.partition() + ", " +
                        "offset: " + eventrecord.offset() + ", " +
                        "key: " + eventrecord.key() + ", " +
                        "timestamp: " + eventrecord.timestamp() + " : " +
                        eventrecord.value());

        } while (true);
    }
}



