#!/bin/sh


if [ -e  datadockDaemon.pid ]; then
    read pid < datadockDaemon.pid
    if ps -p $pid >/dev/null 2>&1; then
        if [ x"$1" = x-kill ]; then
            kill $pid
        elif [ x"$1" = x"-force" ]; then
            true
        else
            echo "pid=$pid is already running!"
            exit 1
        fi
    fi
fi

log_file=daemon.log

java -Ddaemon.pidfile=datadockDaemon.pid -cp `bin/run` dk.dbc.opensearch.components.datadock.DatadockMain <&- >${log_file} 2>&1 &

daemon_pid=$!
sleep 1

if ps -p ${daemon_pid} >/dev/null 2>&1
then
  # daemon is running.
  echo "Datadock[pid=${daemon_pid}] started."
  echo ${daemon_pid} > datadockDaemon.pid
else
  echo "Datadock did not start."
fi
