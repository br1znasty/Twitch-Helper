#!/bin/sh

set -e

echo "Preparing JMeter results directory..."

mkdir -p /load_tests/results
rm -rf /load_tests/results/report
rm -f /load_tests/results/load_tests_result.jtl

echo "Starting JMeter load tests..."

jmeter \
  -n \
  -t /load_tests/load_tests.jmx \
  -l /load_tests/results/load_tests_result.jtl \
  -e \
  -o /load_tests/results/report

echo "JMeter load tests finished."