FROM xq-harbor-ingress.ce027df6a3ed8476bb82b2cd0e6f6f219.cn-beijing.alicontainer.com/library/xq-base-java8:v2020
MAINTAINER hekuangsheng@xueqiu.com

ADD xueqiu-push-user-server/target/*.zip /data/deploy/
ADD shell/deploy_1_stop.sh /data/deploy/deploy_1_stop.sh
ADD shell/deploy_2_replace.sh /data/deploy/deploy_2_replace.sh
ADD shell/deploy_3_start.sh /data/deploy/deploy_3_start.sh

RUN /data/deploy/deploy_2_replace.sh