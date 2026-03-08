#!/bin/bash

FILE="application.pid"

if [ -e $FILE ]; then
  echo "Stop Application..."
  kill -15 `cat application.pid`

  echo "sleep 10s..."
  sleep 10s
fi

#yml
# echo "run using spring.profiles.active=$1"
# echo "run using spring.config.location=$2"
# java -jar ./build/libs/ernscochingAdm.war --spring.profiles.active=$1 --spring.config.location=file:$2

#properties
# echo "run using spring.profiles.active=$1"
# echo "run using spring.config.location=$2"

java -Dspring.profiles.active=$1 -jar ./build/libs/cochingAdm.war
