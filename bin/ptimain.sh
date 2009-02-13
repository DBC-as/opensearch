#!/bin/sh


if [ -e  ptiDaemon.pid ]; then
    read pid < ptiDaemon.pid
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

java -Ddaemon.pidfile=ptiDaemon.pid -cp `bin/run` dk.dbc.opensearch.components.pti2.PTIMain <&- >${log_file} 2>&1 &

daemon_pid=$!
sleep 1

if ps -p ${daemon_pid} >/dev/null 2>&1
then
  # daemon is running.
  echo "Pti[pid=${daemon_pid}] started."
  echo ${daemon_pid} > ptiDaemon.pid
else
  echo "Pti did not start."
fi
