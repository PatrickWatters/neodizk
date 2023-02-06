#!/usr/bin/env bash
max=20
for (( i=1; i <= $max; ++i ))
do
/root/spark-3.1.3-bin-hadoop3.2/bin/spark-submit --class profiler.Profiler --master spark://cluster-master:7077 --conf spark.eventLog.enabled=false --conf spark.eventLog.dir=/tmp/spark-events /home/dizk/target/neodizk-0.2.0.jar sfft $i
    echo "$i"
done