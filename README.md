# flink-java-project

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/677d859f65a64277929491b9e13b5eaa)](https://www.codacy.com/app/dinesh-dart/flink-java-project?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=dineshtrivedi/flink-java-project&amp;utm_campaign=Badge_Grade)
[![Build Status](https://travis-ci.org/dineshtrivedi/flink-java-project.svg?branch=master)](https://travis-ci.org/dineshtrivedi/flink-java-project)
[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/677d859f65a64277929491b9e13b5eaa)](https://www.codacy.com/app/dinesh-dart/flink-java-project?utm_source=github.com&utm_medium=referral&utm_content=dineshtrivedi/flink-java-project&utm_campaign=Badge_Coverage)

flink-java-project tutorial - http://training.data-artisans.com/

Since this project has no frontend I have configured TravisCI to create the github pages showing the coverage. Access coverage page [here](https://dineshtrivedi.github.io/flink-java-project/index.html).

## Tasks
### Task1 - DataStream API Basics
This task consist in implement the Ride Cleansing exercise. Running:
* Start local flink:
```
cd /path/to/flink/installation
./bin/start-local.sh
```

It also starts the WebGUI - [http://localhost:8081](http://localhost:8081).

* Submit a job
```
cd /path/to/flink/installation
./bin/flink run -c com.training.data.artisans.taxi.TaxiRideCleansingRunner /path/to/program/target/flink-java-project-0.1.jar --input "/path/to/nycTaxiRides.gz"
```

* Stoping local flink:
```
cd /path/to/flink/installation
./bin/stop-local.sh
```

### Task2 - DataStream API Time & Windows
This task consist in implement the Popular Places exercise.
* Start local flink:
```
cd /path/to/flink/installation
./bin/start-local.sh
```

It also starts the WebGUI - [http://localhost:8081](http://localhost:8081).

* Submit a job
```
cd /path/to/flink/installation
./bin/flink run -c com.training.data.artisans.taxi.PoupularPlacesMain /path/to/program/target/flink-java-project-0.1.jar
```

* Stoping local flink:
```
cd /path/to/flink/installation
./bin/stop-local.sh
```

### Task3 - DataStream API Time & Windows

* Start Zookeeper (Kafka uses ZooKeeper for distributed coordination) on localhost:2181
```
cd path/to/kafka_2.10-0.10.2.0
./bin/zookeeper-server-start.sh config/zookeeper.properties &
```

* Start a Kafka instance on localhost:9092:
```
cd path/to/kafka_2.10-0.10.2.0
./bin/kafka-server-start.sh config/server.properties &
```

* Stop Kafka and ZooKeeper by calling scripts below in exactly the same order.
```
./bin/kafka-server-stop.sh 
./bin/zookeeper-server-stop.sh
```

## Travis 

It wasn't possible to use TravisCI straight away. Check the problems in the Problems section.

### Problems
I have face problems using flink-training-exercises package as a dependency.

* There is no version 0.10.0 in the [maven repository](https://mvnrepository.com/artifact/com.data-artisans/flink-training-exercises)
* I can't pull from github, there is no mvn-repo branch [link](https://stackoverflow.com/questions/14013644/hosting-a-maven-repository-on-github?rq=1)

### Solution

Fortunately, there is a solution :).

First I have added an install step into .travis.yml
```
install: ./ci/install-flink-training-exercises.sh
```

Essentially, the flink-training-exercise is cloned and built.

Check the ci/install-flink-training-exercises.sh to understand how this process is done. 

## Codacy
You can find the reference [here](https://github.com/codacy/codacy-coverage-reporter#travis-ci)

### Depencencies Ubuntu 16.04
* sudo apt-get install jq
* sudo apt install curl

## Travis updating gh-pages
You can find the reference [here](https://gist.github.com/domenic/ec8b0fc8ab45f39403dd)

### Dependencies Ubuntu 16.04
* sudo apt install ruby
* sudo apt-get install ruby-dev
* sudo gem install travis

### Finding gem
In order to find the instalation path run:
* gem environment | grep "\- INSTALLATION DIRECTORY"

# References
* https://ci.apache.org/projects/flink/flink-docs-release-1.3/dev/best_practices.html
* Travis commit gh-pages - https://gist.github.com/domenic/ec8b0fc8ab45f39403dd