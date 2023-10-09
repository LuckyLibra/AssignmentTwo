@REM ------ TEST SEVEN : DELETE OLD DATA ------ 
@REM A content server sends a PUT request to the aggregation server.
@REM The aggregation server stores the data to the database.
@REM After 30 seconds, the aggregation server sends a GET request to the content server to check if they are alive. 
@REM The content server does not respond because it closed. 
@REM The aggregation server deletes the information sent by that content server.
@REM A client server makes a get request.
@REM The aggregation server sends the data to the client.
@REM The client views the data, the deleted data is not present.
@REM --------------------------------------------------------------------

cd ..
make extra_clean
make
start java -cp . AggregationServer
start java -cp . ContentServer localhost:4567 Input_Files/AdelaideInput.txt

timeout /t 40

start java -cp . GetClient localhost:4567