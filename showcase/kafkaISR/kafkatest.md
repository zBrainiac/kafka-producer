



```
cd infra/kafka_2.12-2.6.0  
./bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --topic test-ISR --partitions 5 --replication-factor 5 --config min.insync.replicas=4 --config unclean.leader.election.enable=false
./bin/kafka-topics.sh --describe --bootstrap-server <server-list/dns>:9092 --topic test-ISR


./bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --topic iot --partitions 5 --replication-factor 5 --config min.insync.replicas=4 --config unclean.leader.election.enable=false
./bin/kafka-topics.sh --describe --bootstrap-server localhost:9092 --topic iot
```

```
cd infra/kafka_2.12-2.6.0  
bin/kafka-topics.sh --delete --bootstrap-server localhost:9092 --topic iot  &&
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 12 --topic iot --config min.insync.replicas=3 &&
bin/kafka-topics.sh --list --bootstrap-server localhost:9092  &&
bin/kafka-topics.sh --describe --bootstrap-server localhost:9092  --topic iot &&
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic iot --group cli
```



```
cd infra/kafka_2.12-2.6.0  &&
bin/kafka-topics.sh --delete --bootstrap-server localhost:9092 --topic iot  &&
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 12 --topic iot --config min.insync.replicas=1 &&
bin/kafka-topics.sh --list --bootstrap-server localhost:9092  &&
bin/kafka-topics.sh --describe --bootstrap-server localhost:9092  --topic iot &&
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic iot --group cli
```