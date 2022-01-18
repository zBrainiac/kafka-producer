package producer;

import com.opencsv.CSVReader;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.security.SecureRandom;
import java.util.Properties;
import java.util.Random;


/**
 * Kafka topic:
 * ./bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 5 --topic CustomerFeedback --config "cleanup.policy=compact" --config "delete.retention.ms=100"  --config "segment.ms=100" --config "min.cleanable.dirty.ratio=0.01"
 *
 * kafka-console-consumer --bootstrap-server localhost:9092 --topic CustomerFeedback --property  print.key=true --from-beginning
 *
 * run:
 * cd /opt/cloudera/parcels/FLINK/lib/flink/examples/streaming &&
 * java -classpath kafka-producer-0.0.1.0.jar producer.KafkaCustomerFeedback localhost:9092
 *
 * @author Marcel Daeppen
 * @version 2021/10/29 11:28
 */

public class KafkaCustomerFeedback {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaCustomerFeedback.class);
    private static final String LOGGERMSG = "Program prop set {}";
    private static String brokerURI = "localhost:9092";
    private static long sleeptime = 1000;
    private static String inputFilePath = "/Users/mdaeppen/GoogleDrive/workspace/kafka-producer/data/consumer_complaints_16952.csv";

    static KafkaProducer<String, String> producer;

    public static void main(String[] args) throws Exception {

        if (args.length == 1) {
            brokerURI = args[0];
            String parm = "'use customized URI' = " + brokerURI + " & 'use default sleeptime' = " + sleeptime + " & 'use default inputFilePath' = " + inputFilePath;
            LOG.info(LOGGERMSG, parm);
        } else if (args.length == 2) {
            brokerURI = args[0];
            inputFilePath = args[1];
            String parm = "'use customized URI' = " + brokerURI + " & 'use customized sleeptime' = " + sleeptime + " & 'use customized inputFilePath' = " + inputFilePath;
            LOG.info(LOGGERMSG, parm);
        } else if (args.length == 3) {
            brokerURI = args[0];
            inputFilePath = args[1];
            setsleeptime(Long.parseLong(args[2]));
            String parm = "'use customized URI' = " + brokerURI + " & 'use customized sleeptime' = " + sleeptime + " & 'use customized inputFilePath' = " + inputFilePath;
            LOG.info(LOGGERMSG, parm);
        } else {
            String parm = "'use default URI' = " + brokerURI + " & 'use default sleeptime' = " + " & 'use customized inputFilePath' = " + inputFilePath;
            LOG.info(LOGGERMSG, parm);
        }

        final Logger logger = LoggerFactory.getLogger(KafkaCustomerFeedback.class);
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerURI);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        config.put(ProducerConfig.CLIENT_ID_CONFIG, "Feeder-KafkaCustomerFeedback");
        config.put(ProducerConfig.ACKS_CONFIG, "all");

        producer = new KafkaProducer<>(config);

        try (CSVReader reader = new CSVReader(new FileReader(inputFilePath))) {
            String[] lineInArray;
            while ((lineInArray = reader.readNext()) != null) {
                String CustomerFeedback = lineInArray[5] ;
                System.out.println(CustomerFeedback);

                ProducerRecord<String, String> eventRecord = new ProducerRecord<>("CustomerFeedback", CustomerFeedback);
                RecordMetadata msg = producer.send(eventRecord).get();

                LOG.info(new StringBuilder().append("Published ")
                        .append(msg.topic()).append("/")
                        .append(msg.partition()).append("/")
                        .append(msg.offset()).append(" : ")
                        .append(CustomerFeedback)
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
        KafkaCustomerFeedback.sleeptime = sleeptime;
    }
}