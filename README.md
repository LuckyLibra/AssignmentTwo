# AssignmentTwo
The automated testing folder contains a README file which describes each of the automated test cases along with their .bat file,
these automated test cases have been designed to show the various functionalities of the system and ensure they correctly operate.
It is suggested to the view these first, then if additional or further testing is required:


To test the files manually: 

To compile run "make" in the root folder

To start the Aggregation Server:
In a new terminal type "java AggregationServer [optional argument: PORT]"
                        ex. java AggregationServer 45679

To start the Content Server:
In a new terminal type "java ContentServer [host_name] [file_location] "
                        ex. java ContentServer localhost:4567 Input_Files/AdelaideInput.txt

To start the client Server:
In a new terminal type "java GetClient [host_name] [optional argument: ID]"
                        (without ID) ex. java GetClient localhost:4567
                        (with ID) ex. java GetClient localhost:4567 IDS12345