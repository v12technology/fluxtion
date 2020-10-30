#!/bin/bash
#script to patch springboot jar with fluxtion classes required for dynamic compilation
set -m
echo "patching springboot fat jar with fluxtion classes"
jar=$JAVA_HOME/bin/jar
java=$JAVA_HOME/bin/java
version=$1

CD=`pwd`
cd `dirname $0`
SCRIPTDIR=`pwd`
extractDir=$SCRIPTDIR/extract/

echo Launch dir  : $SCRIPTDIR
echo Current dir : $CD
echo version     : $version
echo extractDir $extractDir

if [ ! -d $extractDir ] 
then
    echo "Folder does not exist $extractDir extracting classes to patch"
    mkdir $extractDir
    cd $extractDir
    $jar -xvf ../../../api/target/fluxtion-api-%version%.jar
    $jar -xvf ../../../extensions/streaming/api/target/fluxtion-streaming-api-%version%.jar
    $jar -xvf ../../../extensions/text/api/target/fluxtion-text-api-%version%.jar
    $jar -xvf ../disruptor-3.4.2.jar  
fi
cd $CD

echo "patching fluxtion classes"
$jar uf $SCRIPTDIR/../target/fluxtion-integration-$version.jar  -C $extractDir com
echo "completed patching"


