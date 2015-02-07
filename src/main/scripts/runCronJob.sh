#!/bin/bash
output=2
projDir=$(pwd)
#Please replace this path with path from where want to run the ssh comman"
cd $HOME/code/vagrant

echo "Trying to reload the memcache loader"
echo "......................................"
echo $(pwd)

vagrant ssh ld-tm-cron-a -c 'sudo /opt/javaproxy/runtime/memcacheloader --servicemapi --devclass --verbose -a 47400' 2>&1 | tee $projDir/CronLog.log
log=$projDir/CronLog.log

echo $(cat $log)

if cat $log | grep "error:"
 then
     echo "======================================================================"
     echo "Error Occurred : The the data was not successfully loaded on mem cache"
     output=0
else
     echo "======================================================================"
     echo "Success : The mem cache loader was updated successfully"
     output=1

fi

exit $output
