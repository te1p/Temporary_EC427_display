#!/usr/bin/env bash
clear

pids=()

echo "starting server in new process"

echo "waiting for queue-service to come up"
./gradlew :queue-service:runService &
pids+=($!)
echo "server pid is ${pids[0]}"

sleep 10

echo "waiting for finhub-service to come up"
./gradlew :finnhub-service:runServiceWithMockFinhubPreload &
pids+=($!)
echo "server pid is ${pids[1]}"

sleep 30

echo "waiting for monolith-service to come up"
./gradlew :monolith-service:runServer &
pids+=($!)
echo "server pid is ${pids[2]}"

sleep 10

echo "starting server test"
./gradlew :monolith-service:executeServerIntegrationTest

echo "stopping server"
kill -9 "${pids[0]}"
kill -9 "${pids[1]}"
kill -9 "${pids[2]}"