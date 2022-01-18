#!/bin/sh

MY_KAFKA_BASE_DIR="/Users/mdaeppen/infra/kafka"
MY_KAFKA_WORKSPACE_DIR="/Users/mdaeppen/GoogleDrive/workspace/kafka-producer/showcase/kafkaSecure/config"

NO_BROKER="3"

MAX_BROKER_ID=$((NO_BROKER-1))
echo $MAX_BROKER_ID

echo "MY_KAFKA_BASE_DIR = $MY_KAFKA_BASE_DIR"
echo "MY_KAFKA_WORKSPACE_DIR = $MY_KAFKA_WORKSPACE_DIR"
echo "MAX_BROKER_ID = $MAX_BROKER_ID"

echo "$1"
if [ "$1" = "-h" ]; then
  cd || exit
  cd $MY_KAFKA_BASE_DIR || exit

  for i in $(eval echo "{0..$MAX_BROKER_ID}")
  do
    var=$(cat run_KafkaServer"$i".pid)
    echo "${var}"
    kill -9  "${var}"
    rm run_KafkaServer"$i".pid
    rm run_KafkaServer"$i".log
    rm -rf /tmp/kafka-logs"$i"
    echo "clean-up KafkaServer$i - ""${var}"""

  done

elif [ "$1" = "-s" ]; then

  cd || exit
  cd $MY_KAFKA_BASE_DIR || exit

  for i in $(eval echo "{0..$MAX_BROKER_ID}")
  do
    nohup bin/kafka-server-start.sh $MY_KAFKA_WORKSPACE_DIR/server"$i".properties >run_KafkaServer"$i".log &
    echo $! >run_KafkaServer"$i".pid &
    var=$(cat run_KafkaServer"$i".pid)
    echo "start KafkaServer$i - ${var}"
  done

else
  printf "Options: \n -h oder --halt \n -s oder --start \n"
fi