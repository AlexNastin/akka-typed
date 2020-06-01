#!/bin/sh
now=$(date +"%T")
cp -r logs backup/logs-$now
rm -r logs
/c/Work/Demo/AkkaTest/jdk1.8.0_171/bin/java -jar seed-1.0.0-SNAPSHOT.jar &
sleep 5
/c/Work/Demo/AkkaTest/jdk1.8.0_171/bin/java -jar node-1.0.0-SNAPSHOT.jar $1 $2 $3 $4 &