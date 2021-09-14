package producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Properties;
import java.util.Random;


/**
 * Kafka topic:
 * ./bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 5 --topic iot --config "cleanup.policy=compact" --config "delete.retention.ms=100"  --config "segment.ms=100" --config "min.cleanable.dirty.ratio=0.01"
 *
 * kafka-console-consumer --bootstrap-server localhost:9092 --topic iot --property  print.key=true --from-beginning
 *
 * run:
 * cd /opt/cloudera/parcels/FLINK/lib/flink/examples/streaming &&
 * java -classpath kafka-producer-0.0.1.0.jar producer.KafkaIOTSensorSimulator localhost:9092
 *
 * @author Marcel Daeppen
 * @version 2021/08/29 14:28
 */

public class KafkaIOTSensorSimulator {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaIOTSensorSimulator.class);
    private static final Random random = new SecureRandom();
    private static final String LOGGERMSG = "Program prop set {}";
    private static String brokerURI = "localhost:9092";
    private static long sleeptime = 1000;

    static KafkaProducer<String, String> producer;

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

        final Logger logger = LoggerFactory.getLogger(KafkaIOTSensorSimulator.class);
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerURI);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        config.put(ProducerConfig.CLIENT_ID_CONFIG, "Feeder-KafkaIOTSensorSimulator");
        config.put(ProducerConfig.ACKS_CONFIG, "all");
        config.put(ProducerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, "250");
        config.put(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, "300");
        config.put(ProducerConfig.LINGER_MS_CONFIG, "10");
        config.put(ProducerConfig.RETRIES_CONFIG, "3");

        producer = new KafkaProducer<>(config);

        try {
            for (int i = 0; i < 1000000; i++) {

                int sensor_id = random.nextInt(99);
                String key = Integer.toString(sensor_id);

                String value = "{"
                        + "\"sensor_ts\"" + ":" + Instant.now().toEpochMilli() + ","
                        + "\"sensor_id\"" + ":" + sensor_id + ","
                        + "\"sensor_0\"" + ":" + i + ","
                        + "\"sensor_1\"" + ":" + random.nextInt(11) + ","
                        + "\"sensor_2\"" + ":" + random.nextInt(22) + ","
                        + "\"sensor_3\"" + ":" + random.nextInt(33) + ","
                        + "\"sensor_4\"" + ":" + random.nextInt(44) + ","
                        + "\"sensor_5\"" + ":" + random.nextInt(55) + ","
                        + "\"sensor_6\"" + ":" + random.nextInt(66) + ","
                        + "\"sensor_7\"" + ":" + random.nextInt(77) + ","
                        + "\"sensor_8\"" + ":" + random.nextInt(88) + ","
                        + "\"sensor_9\"" + ":" + random.nextInt(99) + ","
                        + "\"sensor_10\"" + ":" + random.nextInt(1010) + ","
                        + "\"sensor_11\"" + ":" + random.nextInt(1111) + ","
                        +  "}";



                ProducerRecord<String, String> eventrecord = new ProducerRecord<>("iot", key, value);

                RecordMetadata msg = producer.send(eventrecord).get();

                LOG.info(new StringBuilder().append("Published ")
                        .append(msg.topic()).append("/")
                        .append(msg.partition()).append("/")
                        .append(msg.offset()).append(" : ")
                        .append(value)
                        .toString());

                Thread.sleep(sleeptime);

            }
        } catch (ProducerFencedException | OutOfOrderSequenceException | AuthorizationException | NotEnoughReplicasException | NetworkException e) {
            // We can't recover from these exceptions, so our only option is to close the producer and exit.
            producer.close();
            logger.error("Error Kafka: {}", e, e);
        } catch (BrokerNotAvailableException e) {
            logger.error("broker not available {}",e, e);
        }
    }



    public static void setsleeptime(long sleeptime) {
        KafkaIOTSensorSimulator.sleeptime = sleeptime;
    }
}