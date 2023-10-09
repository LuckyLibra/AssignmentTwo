@REM --------TEST FIVE : 204 NO CONTENT------------- 
@REM A content server sends a PUT request with no contnet.
@REM The aggregation server sends a 204 Status code (No content)
@REM ------------------------------------------------

cd ..
make extra_clean
make
start java -cp . AggregationServer
start java -cp . ContentServer localhost:4567 Input_Files/NoInput.txt