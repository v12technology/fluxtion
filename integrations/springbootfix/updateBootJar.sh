#!/bin/bash
#script to patch springboot jar with fluxtion classes required for dynamic compilation
set -m
echo "patching springboot fat jar with fluxtion classes"
jar=$JAVA_HOME/bin/jar
java=$JAVA_HOME/bin/java

CD=`pwd`
cd `dirname $0`
SCRIPTDIR=`pwd`
extractDir=$SCRIPTDIR/extract/

echo Launch dir: $SCRIPTDIR
echo Current dir: $CD
echo extractDir $extractDir

if [ ! -d $extractDir ] 
then
    echo "Folder does not exist $extractDir extracting classes to patch"
    mkdir $extractDir
    cd $extractDir
    $jar -xvf ../fluxtion-api-2.6.3-SNAPSHOT.jar
    $jar -xvf ../fluxtion-streaming-api-2.6.3-SNAPSHOT.jar
    $jar -xvf ../fluxtion-text-api-2.6.3-SNAPSHOT.jar
    $jar -xvf ../lombok-1.18.12.jar
    $jar -xvf ../disruptor-3.4.2.jar  
fi
cd $CD

echo "patching lombok classes"
$jar uf $SCRIPTDIR/../target/fluxtion-integration-2.6.3-SNAPSHOT.jar  -C $extractDir lombok

echo "patching fluxtion classes"
$jar uf $SCRIPTDIR/../target/fluxtion-integration-2.6.3-SNAPSHOT.jar  -C $extractDir com
echo "completed patching"


