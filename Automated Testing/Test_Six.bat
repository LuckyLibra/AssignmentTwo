@REM -------TEST SIX : AGGREGATION SERVER CRASH --------- (PASSED)
@REM A content server sends a PUT request to the aggregation server.
@REM The aggregation server creates a database and stores the information there and sends a 201 OK.
@REM The aggregation server crashes.
@REM The aggregation server restarts.
@REM A client server makes a GET request to the aggregation server.
@REM The aggregation server retrieves data from a text file and sends it to the client.
@REM ---------------------------------------------------------------------------------

cd ..

make extra_clean
make

echo Starting AggregationServer...
start java -cp . AggregationServer


echo Starting ContentServer...
start java -cp . ContentServer localhost:4567 Input_Files/AdelaideInput.txt


echo Waiting for servers to start (adjust timeout as needed)...
timeout /t 15

echo Restarting AggregationServer...
start java -cp . AggregationServer

echo Starting GetClient...
start java -cp . GetClient localhost:4567

echo Script completed.