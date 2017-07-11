#!/usr/bin/env bash
set -e  # If occur any error, exit
set -x  # Verbose mode

. ci/codacy_credential_key.sh

java -cp ~/.codacy/repoerter/codacy-coverage-reporter-assembly-latest.jar com.codacy.CodacyCoverageReporter -l Java -r target/site/jacoco-ut/jacoco.xml