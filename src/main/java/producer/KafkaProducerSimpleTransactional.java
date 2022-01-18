package producer;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Properties;

/**
 * Create Kafka Topic:
 * bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 5 --topic kafka_simple &&
 * bin/kafka-topics.sh --list --bootstrap-server localhost:9092 &&
 * bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic kafka_simple
 *
 * output:
 * msg_id=11, Current_time_is=2021-12-06T16:30:37.807Z
 *
 * run:
 * cd /opt/cloudera/parcels/FLINK/lib/flink/examples/streaming &&
 * java -classpath kafka-producer-0.0.1.0.jar producer.KafkaProducerSimpleTransactional localhost:9092
 *
 * @author Marcel Daeppen
 * @version 2021/12/06 17:28
 */

public class KafkaProducerSimpleTransactional {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaProducerSimpleTransactional.class);
    private static final String LOGGERMSG = "Program prop set {}";

    private static String brokerURI = "kafka:9092";
    private static long sleeptime = 1000;

    public static void main(String[] args) throws Exception {

        if (args.length == 1) {
            brokerURI = args[0];
            String parm = "'use customized URI' = " + brokerURI + " & 'use default sleeptime' = " + sleeptime;
            LOG.info(LOGGERMSG, parm);
        } else if (args.length == 2) {
            brokerURI = args[0];
            setsleeptime(Long.parseLong(args[1]));
            String parm = "'use customized URI' = " + brokerURI + " & 'use customized sleeptime' = " + sleeptime;
            LOG.info(LOGGERMSG, parm);
        } else {
            String parm = "'use default URI' = " + brokerURI + " & 'use default sleeptime' = " + sleeptime;
            LOG.info(LOGGERMSG, parm);
        }

        //create kafka producer
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerURI);
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, "Feeder-Kafka-Simple-Transactional");
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        properties.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "simple-transactional");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, "com.hortonworks.smm.kafka.monitoring.interceptors.MonitoringProducerInterceptor");

        try (Producer<String, String> producer = new KafkaProducer<>(properties)) {

            producer.initTransactions();

            for (int i = 0; i < 1000000; i++) {
                producer.beginTransaction();
                String recordValue = "msg_id:" + i + ", Current_time_is:" + Instant.now().toString();

                ProducerRecord<String, String> eventrecord = new ProducerRecord<>("kafka_simple_transactional", recordValue);

                //produce the eventrecord
                RecordMetadata msg = producer.send(eventrecord).get();

                LOG.info(new StringBuilder().append("Published ")
                        .append(msg.topic()).append("/")
                        .append(msg.partition()).append("/")
                        .append(msg.offset()).append(" : ")
                        .append(recordValue)
                        .toString());

                producer.commitTransaction();
                // wait
                Thread.sleep(sleeptime);
            }
            producer.flush();
        }
    }

    public static void setsleeptime(long sleeptime) {
        KafkaProducerSimpleTransactional.sleeptime = sleeptime;
    }

}