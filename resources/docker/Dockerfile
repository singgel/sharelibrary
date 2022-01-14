FROM xq-harbor-ingress.ce027df6a3ed8476bb82b2cd0e6f6f219.cn-beijing.alicontainer.com/library/xq-base-java8:v2020
MAINTAINER sre@xueqiu.com

ENV CONTAINER_ENV {{CONTAINER_ENV}}
ENV CONTAINER_PROJ {{CONTAINER_PROJ}}
ENV BUILD_ZIP_FILE {{BUILD_ZIP_FILE}}
ENV BUILD_UNZIP_DIR {{BUILD_UNZIP_DIR}}

ADD  {{BUILD_ZIP_PATH}}/{{BUILD_ZIP_FILE}}.zip /data/deploy/
ADD  ./deploy_1_stop.sh /data/deploy/deploy_1_stop.sh
ADD  ./deploy_2_replace.sh /data/deploy/deploy_2_replace.sh
ADD  ./deploy_3_start.sh /data/deploy/deploy_3_start.sh

RUN /data/deploy/deploy_2_replace.sh

ENTRYPOINT ["sh","/data/deploy/deploy_3_start.sh"]