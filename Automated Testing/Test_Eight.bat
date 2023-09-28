@REM -------TEST EIGHT : OVERWRITE DATA BASED ON ID-------------
@REM A content server sends a PUT request to the aggregation server.
@REM The aggregation server stores the data to the database.
@REM The content server then sends updated weather information with the same ID.
@REM The aggregation server searches the database for this ID, then overwrites the old data with new data. 
@REM ----------------------------------------------------------------------------

cd ..
make extra_clean
make
start java -cp . AggregationServer
start java -cp . ContentServer localhost:4567 Input_Files/AdelaideInput.txt
start java -cp . ContentServer localhost:4567 Input_Files/AdelaideInputUpdated.txt
start java -cp . GetClient localhost:4567