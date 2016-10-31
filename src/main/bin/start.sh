#!/bin/sh

current_path=$(pwd)
# get the shell's father directory
case "$(uname)" in
	Linux)
		bin_abs_path=$(readlink -f $(dirname $0))
		;;
	*)
		bin_abs_path=$(cd $(dirname $0); pwd)
		;;
esac

base=${bin_abs_path}/..
conf=${base}/conf/config.properties
log4j=${base}/conf/log4j.properties

export LANG=en_US.UTF-8
export BASE=$base

if [ -f $base/bin/process.pid ] ; then
    echo "found process.pid , please run stop.sh first." 2>&2
    exit 1
fi

## set java path
if [ -z "$JAVA" ] ; then
    JAVA=$(which java)
fi

if [ -z "$JAVA" ] ; then
    echo "cannot find a java jdk" 2>&2
    exit 1
fi

case "$#"
in
0 )
    ;;
1 )
    var=$*
    if [ -f $var ] ; then
        conf=$var
    else
        echo "the parameter is not correct."
        exit
    fi;;
2 )
    var=$1
    if [ -f $var ] ; then
        conf=$var
    else
        if [ "$1" = "debug" ] ; then
            DEBUG_PORT=$2
            DEBUG_SUSPEND="n"
            JAVA_DEBUG_OPT="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=$DEBUG_PORT,server=y,suspend=$DEBUG_SUSPEND"
        fi
    fi;;
* )
    echo "the parameter must be tow or less"
    exit;;
esac

str=$(file $JAVA_HOME/bin/java | grep 64-bit)
if [ -n "$str" ] ; then
    JAVA_OPTS="-server -Xms2048m -Xmx3072m -Xmn1024m -XX:SurvivorRatio=2 -XX:PermSize=96m -XX:MaxPermSize=256m -Xss256k -XX:-UseAdaptiveSizePolicy -XX:MaxTenuringThreshold=15 -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:+HeapDumpOnOutOfMemoryError"
else
    JAVA_OPTS="-server -Xms1024m -Xmx1024m -XX:NewSize=256m -XX:MaxNewSize=256m -XX:MaxPermSize=128m"
fi

JAVA_OPTS=" $JAVA_OPTS -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -Dfile.encoding=UTF-8"
CUSTOM_OPTS="-DappName=jlib -Dconfig.conf=$conf -Dconfig.log4j=$log4j"

if [ -e $conf ]
then
    for i in $base/lib/*;
        do CLASSPATH=$i:"$CLASSPATH";
    done
    for i in $base/conf/*;
        do CLASSPATH=$i:"$CLASSPATH";
    done

    cd $bin_abs_path

    echo conf : $conf
    cd $base
    $JAVA $JAVA_OPTS $JAVA_DEBUG_OPT $CUSTOM_OPTS -classpath .:$CLASSPATH com.github.hackerwin7.jlib.utils.executors.ProcessBuilderId 1>>/dev/null 2>&1 &
    echo $! > $base/bin/process.pid
    cd $current_path

    echo "process started......"
else
    echo "conf $conf is not exists!"
fi