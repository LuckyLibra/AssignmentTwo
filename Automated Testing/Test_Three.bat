@REM ------------TEST THREE: PUT AND GET TEST-----------
@REM A content server sends a PUT request.
@REM The aggregation server creates the database and sends a 201 OK.
@REM A GetClient sends a GET Request.
@REM The aggregation server retrieves the data and sends it to the requester.
@REM ---------------------------------------------------------------------


cd ..
make extra_clean
make
start java -cp . AggregationServer
start java -cp . ContentServer localhost:4567 Input_Files/AdelaideInput.txt
start java -cp . GetClient localhost:4567