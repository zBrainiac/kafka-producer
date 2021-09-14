package producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import static java.util.Collections.unmodifiableList;


/**
 * run:
 * cd /opt/cloudera/parcels/FLINK/lib/flink/examples/streaming &&
 * java -classpath kafka-producer-0.0.1.0.jar producer.KafkaOPCSimulator localhost:9092
 *
 * @author Marcel Daeppen
 * @version 2021/08/07 12:28
 */

public class KafkaOPCSimulator {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger LOG = LoggerFactory.getLogger(KafkaOPCSimulator.class);
    private static final Random random = new SecureRandom();
    private static final String LOGGERMSG = "Program prop set {}";
    private static final List<String> tagname_list = unmodifiableList(Arrays.asList(
            "Triangle1", "Triangle4711", "Sinusoid", "Sawtooth"));
    private static final List<String> unit_list = unmodifiableList(Arrays.asList(
            "opc1", "opc2", "opc_APAC", "Hydrocracker"));

    private static String brokerURI = "localhost:9092";
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

        try (Producer<String, byte[]> producer = createProducer()) {
            for (int i = 0; i < 1000000; i++) {
                publishMessage(producer);
                Thread.sleep(sleeptime);
            }
        }
    }

    private static Producer<String, byte[]> createProducer() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerURI);
        config.put(ProducerConfig.CLIENT_ID_CONFIG, "Feeder-OPC");
        config.put(ProducerConfig.ACKS_CONFIG, "1");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        config.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, "com.hortonworks.smm.kafka.monitoring.interceptors.MonitoringProducerInterceptor");
        return new KafkaProducer<>(config);
    }

    private static void publishMessage(Producer<String, byte[]> producer) throws Exception {

        ObjectNode messageJsonObject = jsonObject();
        byte[] valueJson = objectMapper.writeValueAsBytes(messageJsonObject);

        ProducerRecord<String, byte[]> eventrecord = new ProducerRecord<>("opc", valueJson);

        RecordMetadata md = producer.send(eventrecord).get();

        LOG.info(new StringBuilder().append("Published ").append(md.topic()).append("/").append(md.partition()).append("/").append(md.offset()).append(") : ").append(messageJsonObject).toString());
    }

    // build random json object
    private static ObjectNode jsonObject() {

        ObjectNode report = objectMapper.createObjectNode();
        report.put("ts", Instant.now().toString());
        report.put("tagname", tagname_list.get(random.nextInt(tagname_list.size())));
        report.put("unit", unit_list.get(random.nextInt(unit_list.size())));
        report.put("val", (random.nextInt(123456)) / 100000.0);
        return report;
    }

    public static void setsleeptime(long sleeptime) {
        KafkaOPCSimulator.sleeptime = sleeptime;
    }
}