This folder contains several automated tests which can be run. Here is a description of each test:
---------TEST ONE: PUT TEST ------------- (PASSED)
A content server sends a PUT request.
The aggregation server creates the database and sends a 201 OK
-------------------------------------------

-----------TEST TWO: GET TEST ------------ (PASSED)
A GetClient sends a GET request.
The aggregation server retrieves the data and sends it to the requester.
-------------------------------------------------------------------

------------TEST THREE: PUT AND GET TEST------------ (PASSED)
A content server sends a PUT request.
The aggregation server creates the database and sends a 201 OK.
A GetClient sends a GET Request.
The aggregation server retrieves the data and sends it to the requester.
---------------------------------------------------------------------

-------------TEST FOUR: MULTI-PUT AND MULTI-GET REQUEST----------------- (PASSED)
A content server sends a PUT request.
Another content server sends a PUT request for a seperate ID.
The aggregation server creates the database and sends a 201 OK to the first server.
The aggregation server adds to the database and sends a 200 OK to the second server.
A GetClient sends a get request.
A GetClient sends a get request for a specific station ID. 
The aggregation server sends the entire database information to the first client.
The aggregation server sends only the information for that ID to the user. 
------------------------------------------------------

--------TEST FIVE : 204 NO CONTENT------------- (PASSED)
A content server sends a PUT request with no contnet.
The aggregation server sends a 204 Status code (No content)
------------------------------------------------

-------TEST SIX : AGGREGATION SERVER CRASH --------- (PASSED)
A content server sends a PUT request to the aggregation server.
The aggregation server creates a database and stores the information there and sends a 201 OK.
The aggregation server crashes.
The aggregation server restarts.
A client server makes a GET request to the aggregation server.
The aggregation server retrieves data from a text file and sends it to the client.
---------------------------------------------------------------------------------

------ TEST SEVEN : DELETE OLD DATA ------  (PASSED)
A content server sends a PUT request to the aggregation server.
The aggregation server stores the data to the database.
After 30 seconds, the aggregation server sends a GET request to the content server to check if they are alive. 
The content server does not respond because it closed. 
The aggregation server deletes the information sent by that content server.
A client server makes a get request.
The aggregation server sends the data to the client.
The client views the data, the deleted data is not present.
--------------------------------------------------------------------

-------TEST EIGHT : OVERWRITE DATA BASED ON ID------------- (PASSED)
A content server sends a PUT request to the aggregation server.
The aggregation server stores the data to the database.
The content server then sends updated weather information with the same ID.
The aggregation server searches the database for this ID, then overwrites the old data with new data. 
----------------------------------------------------------------------------

-------TEST NINE : LAMPORT CLOCKS -------------
A content server from Sydney sends a PUT Request. 
A content server from Sydney sends a PUT Request with updated information.
The first content server has a processing delay of 15 seconds. 
The thread handling the second content server PUT request is delayed.
The aggregation server finishes handling the thread of the first content server.
The thread handling the second content server is no longer delayed, it completes.
A client server makes it get request. 
The aggregation server sends the data to the client.
The client views the data, the data is from the second content server. 
--------------------------------------------------------------------------------