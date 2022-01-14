#!/bin/bash

# prepare variable
ENV=$CONTAINER_ENV
PRJ=`echo $CONTAINER_PROJ | awk -F"." '{print $NF}'`
PORT="8080"
SCRIPT_START="start_server.sh"
SCRIPT_STOP="stop_server.sh"

# prepare env
export _JAVA_OPTIONS="-Djava.net.preferIPv4Stack=true -Dfile.encoding=UTF-8 -Dxueqiu.env=$ENV -Dxueqiu.service=$PRJ"
mkdir -p /persist/logs
if [ ! -h /data/deploy/$PRJ/logs ];then
  ln -s /persist/logs /data/deploy/$PRJ/logs
fi

nohup /data/deploy/$PRJ/bin/$SCRIPT_START --env=$ENV >> /data/deploy/$PRJ/bin/nohup.out 2>&1 &