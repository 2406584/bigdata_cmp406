FROM apache/hadoop-runner
ARG HADOOP_URL=https://dlcdn.apache.org/hadoop/common/hadoop-3.3.6/hadoop-3.3.6.tar.gz
ARG SPARK_URL=https://archive.apache.org/dist/spark/spark-3.3.1/spark-3.3.1-bin-hadoop3.tgz
WORKDIR /opt
RUN sudo rm -rf /opt/hadoop && curl -LSs -o hadoop.tar.gz $HADOOP_URL && tar zxf hadoop.tar.gz && rm hadoop.tar.gz && mv hadoop* hadoop && rm -rf /opt/hadoop/share/doc
RUN curl ${SPARK_URL} -o spark-3.3.1-bin-hadoop3.tgz && tar zxf spark-3.3.1-bin-hadoop3.tgz && rm -rf spark-3.3.1-bin-hadoop3.tgz
RUN sudo rm -f /etc/yum.repos.d/bintray-rpm.repo && \
    sudo curl -L https://www.scala-sbt.org/sbt-rpm.repo > sbt-rpm.repo && \
    sudo mv sbt-rpm.repo /etc/yum.repos.d/ && \
    sudo yum install -y sbt && \
    sudo yum clean all
WORKDIR /opt/hadoop
ADD log4j.properties /opt/hadoop/etc/hadoop/log4j.properties
RUN sudo chown -R hadoop:users /opt/hadoop/etc/hadoop/*
ENV HADOOP_CONF_DIR /opt/hadoop/etc/hadoop
