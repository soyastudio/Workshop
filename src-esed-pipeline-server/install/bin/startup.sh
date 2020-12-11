#!/bin/bash
java -jar esed-pipeline-server.jar --spring.config.location=../conf/server-config.properties > ../log/log.txt
echo $! > pid.txt