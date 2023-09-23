@REM -----------TEST TWO: GET TEST ------------ (PASSED)
@REM A GetClient sends a GET request.
@REM The aggregation server retrieves the data and sends it to the requester.
@REM -------------------------------------------------------------------


cd ..
start java -cp . AggregationServer
start java -cp . GetClient localhost:4567