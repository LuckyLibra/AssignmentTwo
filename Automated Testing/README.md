This folder contains several automated tests which can be run. Here is a description of each test:

-----TEST ONE: PUT TEST -----
A content server sends a PUT request to the aggregation server.
The aggregation server creates a database and stores the information there and sends a 201 OK.
---------------------------------

-----TEST TWO : PUT and GET Test -----
A content server sends a PUT request to the aggregation server.
The aggregation server creates a database and stores the information there and sends a 201 OK.
A client server makes a GET request which successfully retrieves this data.
---------------------------------------------------------------------

----TEST THREE : Two Content Servers ------
A content server sends a PUT request to the aggregation server.
The aggregation server creates a database and stores the information there and sends a 201 Response.
A second content server sends a PUT request to the aggregation server.
The aggregation server adds it to the database and sends a 200 OK response.
A client server makes a GET request which retrieves two JSON objects. 
---------------------------------------------------------------------

---- TEST FOUR : 204 No Content ----
A content server sends a PUT request with no content. 
The aggregation server sends a 204 Status Code (No content)
---------------------------------------

----- TEST FIVE : Aggregation Server Crash -----
A content server sends a PUT request to the aggregation server.
The aggregation server creates a database and stores the information there and sends a 201 OK.
The aggregation server crashes.
The aggregation server restarts.
A client server makes a GET request to the aggregation server.
The aggregation server retrieves data from a text file and sends it to the client.
-------------------------------------------------------------------------------

------ TEST SIX : Delete Old Data ------
A content server sends a PUT request to the aggregation server.
The aggregation server stores the data to the database.
After 30 seconds, the aggregation server sends a GET request to the content server to check if they are alive. 
The content server does not respond because it closed. 
The aggregation server deletes the information sent by that content server.
A client server makes a get request.
The aggregation server sends the data to the client.
The client views the data, the deleted data is not present.
--------------------------------------------------------------------

-------TEST SEVEN : Overwrite Data Based on ID-------------
A content server sends a PUT request to the aggregation server.
The aggregation server stores the data to the database.
The content server then sends updated weather information with the same ID.
The aggregation server searches the database for this ID, then overwrites the old data with new data. 