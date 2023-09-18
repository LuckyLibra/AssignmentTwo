import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.regex.*;

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
    private Socket client_socket; // socket
    private int threadID; // thread for each connect made, this way hosts are tracked
    private static int counter = 1; // counter which is increased per thread

    // function call to handle get requests
    public HandleRequest(Socket client_socket) {
        this.client_socket = client_socket; // socket connection
    }

    public void run() {
        try {
            StringBuilder request_from_file = new StringBuilder();
            BufferedReader read_file = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
            PrintWriter write = new PrintWriter(client_socket.getOutputStream(), true); // get output

            String incoming_message; // read
            while ((incoming_message = read_file.readLine()) != null) { // While file is not empty, append to string
                request_from_file.append(incoming_message + "\r\n");
            }

            String total_message = request_from_file.toString(); // complete message as string

            if (total_message.contains("PUT")) { // Call put function for put request from contentserver
                putResponse(write, request_from_file);
            } else if (total_message.contains("GET")) { // call get for request from client
                getResponse(write, request_from_file);
            } else {
                write.println("HTTP/1.1 400"); // ERROR
            }

            client_socket.close(); // finally close thread!
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void putResponse(PrintWriter write, StringBuilder read) { // takes read and write
        // Check if there is an Aggregation Database if not create the file
        File database = new File("aggregationDatabase.txt");

        if (database.exists()) { // first check if file exists
            if (database.length() == 0) { // If file exists but has no content
                boolean result = insertIntoFile(read); // Insert json data into file

                if (result == true) {
                    write.println("HTTP/1.1 201 OK "); // Sends 201 OK Response to content server
                } else {
                    write.println("HTTP/1.1 204 No Content");
                }
            }

            else if (database.length() > 0) { // Database already has data, so we have to search for ID to determine
                                              // overwrite
                try {
                    String request_id = findRequestID(read);
                    BufferedReader read_database = new BufferedReader(new FileReader(database)); // Read database
                    String line_from_database;
                    boolean host_id_exists = false;

                    while ((line_from_database = read_database.readLine()) != null) {
                        // data id from file
                        if (line_from_database.contains("id:" + request_id)) { // Old data to overwrite located
                            host_id_exists = true;
                            break;
                        }
                    }

                    if (host_id_exists == true) {
                        removeFromDatabase(this.threadID); // ID to remove from database
                        insertIntoFile(read); // After 'old' data is removed, add new update
                    } else {
                        insertIntoFile(read); // If ID not present, add to database
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Aggregation Database does not exist. Creating database.");
            createFile();
            insertIntoFile(read);
        }
    }

    public void getResponse(PrintWriter write, StringBuilder read) {
        // do stuff
    }

    public boolean checkAlive(PrintWriter write, StringBuilder read) { // sends a get request that checks if the server
                                                                       // is alive after waiting a maximum of five
                                                                       // seconds
        // sends a get request to the content server adn the content server can return a
        // body containing the message "I am alive"

        return false;
    }

    public void createFile() { // creates databaseAggregation.txt file
        File new_database = new File("aggregationDatabase.txt");

        try {
            new_database.createNewFile(); // Create new file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean insertIntoFile(StringBuilder read) { // Inserts data into databaseAggregation.txt file
        // Inserts a json array into a aggregation database text file
        Pattern regex = Pattern.compile("\\{[^{}]*\\}"); // regex for locating json
        Matcher json_obj = regex.matcher(read);

        if (!json_obj.find()) { // Error detected! no relevant content! 400 error
            return false; // if unsuccessful return false
        }

        try { // write to the aggregationDatabase file
            BufferedWriter write_to_file = new BufferedWriter(new FileWriter("aggregationDatabase.txt"));
            write_to_file.write(json_obj.group());
            write_to_file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true; // if successful insert return true
    }

    public void removeFromDatabase(Integer ID_to_remove) {

    }

    public String findRequestID(StringBuilder read) { // This function locates the ID value of a json object which has
                                                      // been converted to a string
        Pattern regex = Pattern.compile("id:" + "([^\\n]+)");
        Matcher id_value = regex.matcher(read);
        return id_value.group(1).trim();
    }

}
