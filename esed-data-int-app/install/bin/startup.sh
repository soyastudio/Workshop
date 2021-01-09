#!/bin/bash
java -jar esed-data-int-app.jar --spring.config.location=../conf/application.yaml > ../log/log.txt
echo $! > pid.txt