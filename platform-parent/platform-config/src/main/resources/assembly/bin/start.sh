#!/bin/bash
cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`
CONF_DIR=$DEPLOY_DIR/conf

SERVER_NAME=`sed '/^spring.application.name/!d;s/.*: //' conf/config/application-*.yml | tr -d '\r'`
SERVER_PORT=`sed '/server.port/!d;s/.*: //' conf/config/application-*.yml | tr -d '\r'`
SERVER_MAIN=`sed '/server.main/!d;s/.*: //' conf/config/application-*.yml | tr -d '\r'`
SERVER_JVM=`sed '/server.jvm/!d;s/.*: //' conf/config/application-*.yml | tr -d '\r'`
SERVER_PROFILE=`sed '/active:/!d;s/.*: //' conf/application-*.yml | tr -d '\r'`
SERVER_IP=`ip addr | grep 'state UP' -A2 | tail -n1 | awk '{print $2}' | awk -F"/" '{print $1}'`

#默认内存参数
jvmArgs="-Xmx2g -Xms256m"

if [ -n "${SERVER_JVM}" ]; then
    jvmArgs="${SERVER_JVM}"
fi

jvmArgs="${jvmArgs} -Dspring.profiles.active=${SERVER_PROFILE} -Dserver.ip=${SERVER_IP} -Dserver.code=${SERVER_NAME}"

echo "开始启动服务：${SERVER_NAME}，启动端口：${SERVER_PORT}，启动jvm参数：${jvmArgs}"

LOGS_FILE=""

if [ -z "$SERVER_NAME" ]; then
    SERVER_NAME=`hostname`
fi
PIDS=`ps -ef | grep java | grep "$CONF_DIR" |awk '{print $2}'`
if [ -n "$PIDS" ]; then
    echo "ERROR: The $SERVER_NAME 服务启动失败，原服务未停止，若是重启操作，请检查启动用户是否正确？"
    echo "PID: $PIDS"
    exit 1
fi

if [ -n "$SERVER_PORT" ]; then
    SERVER_PORT_COUNT=`netstat -tln | grep ":$SERVER_PORT " | wc -l`
    if [ $SERVER_PORT_COUNT -gt 0 ]; then
        echo "ERROR: The $SERVER_NAME port $SERVER_PORT already used!端口被占用"
        exit 1
    fi
fi

LOGS_DIR=""
if [ -n "$LOGS_FILE" ]; then
    LOGS_DIR=`dirname $LOGS_FILE`
else
    LOGS_DIR=$DEPLOY_DIR/logs
fi
if [ ! -d $LOGS_DIR ]; then
    mkdir $LOGS_DIR
fi
STDOUT_FILE=$LOGS_DIR/stdout.log
#STDOUT_FILE=/dev/null

LIB_DIR=$DEPLOY_DIR/lib
#LIB_JARS=`ls $LIB_DIR|grep .jar|awk '{print "'$LIB_DIR'/"$0}'|tr "\n" ":"`

#平台包先加载(项目lib下的都是平台包)
#FINS_LIB_JARS=`ls $LIB_DIR|grep fins-|awk '{print "'$LIB_DIR'/"$0}'|tr "\n" ":"`
LIB_JARS=$FINS_LIB_JARS:$DEPLOY_DIR/lib/*

# 查找公共jar包目录
dir="$DEPLOY_DIR"
COMMON_LIBS_JARS="";
# 不断向上层目录查找，直到找到"platform-common-libs"目录或到达根目录
while [ "$dir" != "/" ]; do
    if [ -d "$dir/platform-common-libs" ]; then
        COMMON_LIBS_JARS="$dir/platform-common-libs/*"
        break
    fi
	cd ../
	if [ $? -ne 0 ]; then
	    echo "没有权限，上层目录未找到platform-common-libs目录"
        break
    fi
	dir=`pwd`
done

echo "platform-common-libs路径:${COMMON_LIBS_JARS}"
cd $DEPLOY_DIR

JAVA_OPTS=" -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true "
JAVA_DEBUG_OPTS=""
if [ "$1" = "debug" ]; then
    JAVA_DEBUG_OPTS=" -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n "
fi
JAVA_JMX_OPTS=""
if [ "$1" = "jmx" ]; then
    JAVA_JMX_OPTS=" -Djava.rmi.server.hostname=${SERVER_IP} -Dcom.sun.management.jmxremote.port=1099 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false "
fi
JAVA_MEM_OPTS=""
BITS=`java -version 2>&1 | grep -i 64-bit`
if [ -n "$BITS" ]; then
    JAVA_MEM_OPTS=" -server ${jvmArgs} -Xmn256m -Xss256k -XX:+DisableExplicitGC "
else
    JAVA_MEM_OPTS=" -server ${jvmArgs} -Xmn256m -Xss256k -XX:+DisableExplicitGC "
fi

echo -e "Starting the $SERVER_NAME ...\c"
nohup java $JAVA_OPTS $JAVA_MEM_OPTS $JAVA_DEBUG_OPTS $JAVA_JMX_OPTS $* -classpath $DEPLOY_DIR:$CONF_DIR:$LIB_JARS:$COMMON_LIBS_JARS $SERVER_MAIN > $STDOUT_FILE 2>&1 &

COUNT=0
NUM=0
TOTAL=60
while [ $COUNT -lt 1 ]; do
    echo -e ".\c"
    sleep 1
    if [ -n "$SERVER_PORT" ]; then
          COUNT=`netstat -an | grep ":$SERVER_PORT " | grep LISTEN |wc -l`
    else
    	COUNT=`ps -ef | grep java | grep "$DEPLOY_DIR" | awk '{print $2}' | wc -l`
    fi
    NUM=`expr $NUM + 1`
    if [ $NUM -eq $TOTAL ]; then
        printf "\n"
        echo "[ERROR]: 应用[$SERVER_NAME]在${TOTAL}s内未能成功启动，请手工检查！！"
        echo "查看启动日志: $STDOUT_FILE"
        cat $STDOUT_FILE
        break
    fi
    if [ $COUNT -gt 0 ]; then
        echo "服务启动成功... start success!"
        PIDS=`ps -ef | grep java | grep "$DEPLOY_DIR" | awk '{print $2}'`
        echo "PIDS:$PIDS"
        break
    fi
done
