@REM ---------TEST ONE: PUT TEST ------------- (PASSED)
@REM A content server sends a PUT request.
@REM The aggregation server creates the database and sends a 201 OK
@REM -------------------------------------------


cd ..
make extra_clean
make
start java -cp . AggregationServer
start java -cp . ContentServer localhost:4567 Input_Files/AdelaideInput.txt