#!/usr/bin/env bash

. ci/utils.sh

if [ -z "${KAFKA_HOME}" ]
then
    echo_red "Environment variable KAFKA_HOME not defined."
else
    dorun "$KAFKA_HOME/bin/zookeeper-server-start.sh $KAFKA_HOME/config/zookeeper.properties &"
    dorun "$KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_HOME/config/server.properties &"
fi

#./bin/zookeeper-server-start.sh config/zookeeper.properties &
#./bin/kafka-server-start.sh config/server.properties &