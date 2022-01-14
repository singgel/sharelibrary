#!/bin/bash

# prepare variable
ENV=$CONTAINER_ENV
PRJ=`echo $CONTAINER_PROJ | awk -F"." '{print $NF}'`
IP=$CONTAINER_IP_ADDR
PORT="8080"
SCRIPT_START="start_server.sh"
SCRIPT_STOP="stop_server.sh"

# stop
if [ -f /data/deploy/$PRJ/bin/$SCRIPT_STOP ] ;then
    /data/deploy/$PRJ/bin/$SCRIPT_STOP
fi

# delete from nginx
WARDEN="http://op.inter.snowballfinance.com/warden"
if [ "x$ENV" = "xrelease" ] ;then
  curl -A"container $IP" -XDELETE $WARDEN/api/upstream/$CONTAINER_REGION"_production"/$PRJ/$ENV/$IP/$PORT
else
  curl -A"container $IP" -XDELETE $WARDEN/api/upstream/$CONTAINER_REGION"_"$ENV/$PRJ/production/$IP/$PORT
fi