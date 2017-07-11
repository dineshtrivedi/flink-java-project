#!/usr/bin/env bash

if [ ! -d "~/flink-training-exercises" ]; then
  git clone https://github.com/dataArtisans/flink-training-exercises.git ~/
fi

cd ~/flink-training-exercises
git reset --hard
git checkout .
git pull
mvn clean install