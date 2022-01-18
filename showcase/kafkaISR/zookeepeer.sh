#!/bin/sh

MY_KAFKA_BASE_DIR="/Users/mdaeppen/infra/kafka"
MY_KAFKA_WORKSPACE_DIR="/Users/mdaeppen/GoogleDrive/workspace/kafka-producer/showcase/kafkaISR/config"
NO_ZK="3"

echo "MY_KAFKA_BASE_DIR = $MY_KAFKA_BASE_DIR"
echo "MY_KAFKA_WORKSPACE_DIR = $MY_KAFKA_WORKSPACE_DIR"

echo "$1"
if [ "$1" = "-h" ]; then
  cd || exit
  cd $MY_KAFKA_BASE_DIR || exit

  for i in $(eval echo "{1..$NO_ZK}")
  do

    var=$(cat run_zookeeper"$i".pid)
    kill -9  "${var}"
    rm run_zookeeper"$i".pid
    rm run_zookeeper"$i".log
    rm -rf $MY_KAFKA_BASE_DIR/zkdata/zk"$i"/version-*
    echo "clean-up zookeeper$i - ""${var}"""
  done

elif [ "$1" = "-s" ]; then

  cd || exit
  cd $MY_KAFKA_BASE_DIR || exit

  for i in $(eval echo "{1..$NO_ZK}")
  do
    nohup bin/zookeeper-server-start.sh $MY_KAFKA_WORKSPACE_DIR/zookeeper"$i".properties >run_zookeeper"$i".log &
    echo $! >run_zookeeper"$i".pid &
    var=$(cat run_zookeeper"$i".pid)
    echo "start zookeeper$i - ${var}" 
  done

else
  printf "Options: \n -h oder --halt \n -s oder --start \n"
fi