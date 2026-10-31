#!/bin/bash
cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`
CURRENT_DIR=`basename $DEPLOY_DIR`
BAK_POSTFIX=-`date +%Y%m%d%H%M%S`-bak.zip
BAK_NAME=$CURRENT_DIR$BAK_POSTFIX
BAK_FULL_NAME=$DEPLOY_DIR$BAK_POSTFIX

SERVER_NAME=`sed '/^spring.application.name/!d;s/.*://' conf/config/bootstrap.yml | tr -d '\r'`

if [ -z "$SERVER_NAME" ]; then
    SERVER_NAME=`hostname`
fi

cd ..
zip -r $CURRENT_DIR$BAK_POSTFIX $CURRENT_DIR

if [ -f "$BAK_FULL_NAME" ];then
echo "[INFO]: 应用[$SERVER_NAME]备份成功,备份文件名$BAK_NAME!!"
else
echo "应用[$SERVER_NAME]备份失败!!"
fi

rm -r $DEPLOY_DIR;
if [ ! -d "$CURRENT_DIR" ];then
echo "[INFO]: 应用[$CURRENT_DIR]删除成功!!"
fi
unzip -o $CURRENT_DIR-bin.zip
if [ -d "$DEPLOY_DIR" ];then
echo "[INFO]: 应用[$CURRENT_DIR]解压成功!!"
fi
