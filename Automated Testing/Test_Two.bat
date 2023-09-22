cd ..
start java -cp . AggregationServer
start java -cp . GetClient localhost:4567
ping 127.0.0.1 -n 6 > nul