import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;

public class AggregationServer {
    public static void main(String[] args) {
        int port = 4567; // default number
        int lamport_clock = 0; // lamport clock

        if (args.length > 0) { // If a port number was in the command line
            port = Integer.parseInt(args[0]); // Update port number to command line arg
        }

        try {
            System.out.print("Aggregation Server is running at " + port + "\n");
            ServerSocket serverSocket = new ServerSocket(port); // open socket

            while (true) {
                Socket client_socket = serverSocket.accept(); // wait for connections
                HandleRequest handleRequest = new HandleRequest(client_socket); // Use class to create a new thread so
                                                                                // threads can be handled seperately
                Thread thread = new Thread(handleRequest);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class HandleRequest implements Runnable { // Runnable allows thread execution
    // socket here as a variable in the class
    private Socket client_socket; //socket
    private int threadID; // thread for each connect made, this way hosts are tracked
    private static int counter = 1; // counter which is increased per thread


    // function call to handle get requests
    public HandleRequest(Socket client_socket) {
        this.client_socket = client_socket;
        counter++;
        this.threadID = counter; // counter is incremented which represents new host found

    }

    public void run() {
        try {
            StringBuilder request_from_file = new StringBuilder();
            BufferedReader read_file = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
            PrintWriter write = new PrintWriter(client_socket.getOutputStream(), true); // get output

            String incoming_message; //read
            while ((incoming_message = read_file.readLine()) != null ) { //While file is not empty, append to string
                request_from_file.append(incoming_message);
            }

            String total_message = request_from_file.toString(); // complete message as string
            System.out.println("Received message " + total_message); //debug 

            if (total_message.contains("PUT")) { //Call put function for put request from contentserver
                putResponse(write, read_file);
            }
            else if (total_message.contains("GET")){ //call get for request from client
                getResponse(write, read_file);
            }
            else {
                //ERROR! RETURN 400 RESPONSE CODE
            }

            client_socket.close(); //finally close thread!
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void putResponse(PrintWriter write, BufferedReader read) { //takes read and write
        //Check if there is an Aggregation Database if not create the file
        File database = new File("aggregationDatabase.txt");
        int host_id = this.threadID;

        if (database.exists()) { //first check if file exists 
            if (database.length() == 0) {
                String success_message = "{'success':'true'}";
                //First time weather data is received you should return status 201 - HTTP_CRATED
                write.println(
                    "HTTP/1.1 201 OK \r\n" +
                    "Content.Length: " + success_message.length() + "\r\n" +
                    "Content-Type: application/json \r\n " +
                    success_message 
                ); //Sends 201 OK Response to content server 
            }
            else if (database.length() > 0) { //Database already has data, so we have to search for ID to determine overwrite
                

            }
        } else {
            System.out.println("Aggregation Database does not exist, creating aggregation database");
            //TODO: Create file "aggregationdatabase.txt"
        }







        //If second update, 200 response code


        //If Put request but not content, 204 status code

    }

    public void getResponse(PrintWriter write, BufferedReader read) {
        //do stuff
    }


    public boolean checkAlive(PrintWriter write, BufferedReader read)  { //sends a get request that checks if the server is alive
        //sends a get request to the content server adn the content server can return a body containing the message "I am alive"

        return false;
    }

}
