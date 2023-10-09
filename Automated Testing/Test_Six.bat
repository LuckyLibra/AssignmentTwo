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

start java -cp . AggregationServer

start java -cp . ContentServer localhost:4567 Input_Files/AdelaideInput.txt

timeout /t 4

taskkill /im java.exe /f /t

start java -cp . AggregationServer

start java -cp . GetClient localhost:4567
