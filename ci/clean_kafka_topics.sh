#!/usr/bin/env bash

. ci/utils.sh

if [[ -z "${KAFKA_HOME}" ]]; then
    echo_red "Environment variable KAFKA_HOME not defined."
else
  dorun "$KAFKA_HOME/bin/kafka-server-stop.sh"
  dorun "$KAFKA_HOME/bin/zookeeper-server-stop.sh"

  dorun "rm -rf /tmp/kafka_logs"
fi