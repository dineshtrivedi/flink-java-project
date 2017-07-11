#!/usr/bin/env bash
set -e  # If occur any error, exit
set -x  # Verbose mode

mkdir -p ~/.codacy/repoerter
wget -O ~/.codacy/repoerter/codacy-coverage-reporter-assembly-latest.jar $(curl https://api.github.com/repos/codacy/codacy-coverage-reporter/releases/latest | jq -r .assets[0].browser_download_url)