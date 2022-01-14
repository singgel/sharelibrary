#!/bin/bash

# prepare variable
ENV=$CONTAINER_ENV
PRJ=`echo $CONTAINER_PROJ | awk -F"." '{print $NF}'`
PORT="8080"
SCRIPT_START="start_server.sh"
SCRIPT_STOP="stop_server.sh"

# stop
if [ -f /data/deploy/$PRJ/bin/$SCRIPT_STOP ] ;then
    /data/deploy/$PRJ/bin/$SCRIPT_STOP
fi