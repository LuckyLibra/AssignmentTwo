@REM -------TEST NINE : LAMPORT CLOCKS -------------
@REM A content server from Sydney sends a PUT Request. 
@REM A content server from Sydney sends a PUT Request with updated information that it is raining now.
@REM The first content server has a processing delay of 7 seconds. 
@REM The thread handling the second content server PUT request is put on hold until the first task has finished.
@REM The aggregation server finishes handling the thread of the first content server.
@REM The thread handling the second content server is no longer delayed, it completes.
@REM A client server makes it get request. 
@REM The aggregation server sends the data to the client.
@REM The client views the data, the data is from the second content server with the update that it is raining now. 
--------------------------------------------------------------------------------

cd ..
make extra_clean
make
start java -cp . AggregationServer
start java -cp . ContentServer localhost:4567 Input_Files/SydneyInput.txt
start java -cp . ContentServer localhost:4567 Input_Files/SydneyInputUpdated.txt

timeout /t 20
start java -cp . GetClient localhost:4567
