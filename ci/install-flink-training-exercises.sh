#!/usr/bin/env bash

. ci/utils.sh

if [ ! -d "$HOME/flink-training-exercises" ]; then
    echo_red "\nflink-training-exercises does not exist.\n"
    echo_green "Cloning...\n"
    dorun "git clone https://github.com/dataArtisans/flink-training-exercises.git $HOME/flink-training-exercises"
    cd ~/flink-training-exercises
else
    echo_green "\nflink-training-exercises exists already.\n"

    cd ~/flink-training-exercises
    echo_green "\nCleaning repository\n"
    dorun "git reset --hard"
    dorun "git checkout ."
    echo_green "\nPulling changes\n"
    dorun "git pull"
fi

dorun "mvn clean install"
