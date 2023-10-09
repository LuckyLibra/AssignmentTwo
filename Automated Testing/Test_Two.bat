@REM -----------TEST TWO: GET TEST ------------
@REM Designed to be made after test one
@REM A GetClient sends a GET request.
@REM The aggregation server retrieves the data and sends it to the requester.
@REM -------------------------------------------------------------------


cd ..
start java -cp . AggregationServer
start java -cp . GetClient localhost:4567