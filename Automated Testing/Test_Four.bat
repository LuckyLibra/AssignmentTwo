@REM -------------TEST FOUR: MULTI-PUT AND MULTI-GET REQUEST----------------- (PASSED)
@REM A content server sends a PUT request.
@REM Another content server sends a PUT request for a seperate ID.
@REM The aggregation server creates the database and sends a 201 OK to the first server.
@REM The aggregation server adds to the database and sends a 200 OK to the second server.
@REM A GetClient sends a get request.
@REM A GetClient sends a get request for a specific station ID. 
@REM The aggregation server sends the entire database information to the first client.
@REM The aggregation server sends only the information for that ID to the user. 
@REM ------------------------------------------------------


cd ..
make extra_clean
make
start java -cp . AggregationServer
start java -cp . ContentServer localhost:4567 Input_Files/AdelaideInput.txt
start java -cp . ContentServer localhost:4567 Input_Files/MelbourneInput.txt
start java -cp . GetClient localhost:4567
start java -cp . GetClient localhost:4567 IDS12345