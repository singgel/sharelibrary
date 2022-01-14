#!/bin/bash

# prepare variable
ENV=$CONTAINER_ENV
PRJ=`echo $CONTAINER_PROJ | awk -F"." '{print $NF}'`
IP=$CONTAINER_IP_ADDR
PORT="8080"
SCRIPT_START="start_server.sh"
SCRIPT_STOP="stop_server.sh"

# prepare env
export _JAVA_OPTIONS="-Djava.net.preferIPv4Stack=true -Dfile.encoding=UTF-8 -Dxueqiu.env=$ENV -Dxueqiu.service=$PRJ -Dxueqiu.ip=$IP"
mkdir -p /persist/logs
if [ ! -h /data/deploy/$PRJ/logs ];then
  ln -s /persist/logs /data/deploy/$PRJ/logs
fi

nohup /data/deploy/$PRJ/bin/$SCRIPT_START --env=$ENV >> /data/deploy/$PRJ/bin/nohup.out 2>&1 &

# regist to nginx
#WARDEN="http://op.inter.snowballfinance.com/warden"
#if [ "x$ENV" = "xrelease" ] ;then
#  curl -i -A"container $IP" -XPUT -d "" $WARDEN/api/upstream/$CONTAINER_REGION"_production"/$PRJ/$ENV/$IP/$PORT
#else
#  curl -i -A"container $IP" -XPUT -d "" $WARDEN/api/upstream/$CONTAINER_REGION"_"$ENV/$PRJ/production/$IP/$PORT
#fi
#cd /data/tools/
#tar -xzf filebeat.tar.gz
#sleep 3
#/data/tools/filebeat/filebeat -e -c /data/tools/filebeat/filebeat.yml &