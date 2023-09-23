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

class HandleRequest extends Thread { // Runnable allows thread execution
    // socket here as a variable in the class
    private Socket client_socket; // socket

    // function call to handle get requests
    public HandleRequest(Socket client_socket) {
        this.client_socket = client_socket; // socket connection
    }

    public void run() {
        try (
                BufferedReader read_file = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
                PrintWriter write = new PrintWriter(client_socket.getOutputStream(), true); // get output
        ) {
            StringBuilder request_from_file = new StringBuilder();

            String incoming_message; // read

            while (!(incoming_message = read_file.readLine()).isEmpty()) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void putResponse(PrintWriter write, StringBuilder read) { // takes read and write
        // Check if there is an Aggregation Database if not create the file
        File database = new File("aggregationDatabase.txt");

        try {

            if (database.exists()) { // first check if file exists
                if (database.length() == 0) { // If file exists but has no content
                    boolean result = insertIntoFile(read); // Insert json data into file

                    if (result == true) {
                        write.println("HTTP/1.1 200 OK "); // Sends 200 OK Response to content server
                        this.client_socket.close(); // close socket after sending response
                    } else {
                        write.println("HTTP/1.1 204 No Content");
                        this.client_socket.close(); // close socket after sending response
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
                            removeFromDatabase(request_id); // ID to remove from database
                            insertIntoFile(read); // After 'old' data is removed, add new update
                            write.println("HTTP/1.1 200 OK "); // Sends 200 OK Response to content server
                            this.client_socket.close(); // close socket after sending response
                        } else {
                            insertIntoFile(read); // If ID not present, add to database
                            write.println("HTTP/1.1 200 OK "); // Sends 200 OK Response to content server
                            this.client_socket.close(); // close socket after sending response
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                System.out.println("Aggregation Database does not exist. Creating database.");
                createFile();
                boolean check = insertIntoFile(read);
                if (check) {
                    write.println("HTTP/1.1 201 OK "); // Sends 201 OK Response to content server
                } else {
                    write.println("HTTP/1.1 204 No Content");
                }
                this.client_socket.close(); // close socket after sending response
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getResponse(PrintWriter write, StringBuilder read) {
        String database;
        if (read.toString().contains("/weather.json/")) { // Second slash signals a station ID is requested
            String[] words = ((read.toString().split(" "))[1]).split("/");
            String id_found = words[2];

            if (id_found.length() > 0) {
                System.out.println("GET Request received for weather station: " + id_found);
                database = retrieveDatabase(id_found);
            } else {
                write.println("Error 400");
                return;
            }

        } else if (read.toString().contains("/weather.json")) { // No slash signal no station ID, therefore return all
            System.out.println("GET Request received for weather station.");
            database = retrieveDatabase(null); // Take null ID as argument

        } else {
            write.println("Error 400 Bad Request");
            return;
        }

        System.out.println("Sending response to GET request");
        write.println(database);
        try {
            this.client_socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

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

        try { // write to the aggregationDatabase file
            if (!json_obj.find() || json_obj.group().length() <= 4) { // Error detected! no relevant content! 400 error
                return false; // if unsuccessful return false
            }

            BufferedWriter write_to_file = new BufferedWriter(new FileWriter("aggregationDatabase.txt", true));
            write_to_file.write(json_obj.group());
            write_to_file.newLine();
            write_to_file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true; // if successful insert return true
    }

    public void removeFromDatabase(String ID_to_remove) {

    }

    public String findRequestID(StringBuilder read) { // This function locates the ID value of a json object which has
                                                      // been converted to a string
        Pattern regex = Pattern.compile("id:" + "([^\\n]+)");
        Matcher id_value = regex.matcher(read);

        if (id_value.find()) {
            return id_value.group(1).trim();
        }
        return "";
    }

    // This function takes a string
    public String retrieveDatabase(String id) {
        if (id == null) { // retrieve entire database
            StringBuilder file_content = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(new FileReader("aggregationDatabase.txt"))) {
                String new_line;
                while ((new_line = reader.readLine()) != null) { // while file not empty add content
                    file_content.append(new_line + "\r\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file_content.toString(); // Return file content as string

        } else { // if there is a selected station
            try {
                StringBuilder selected_data = new StringBuilder(); // The data requested specifically which matches the
                                                                   // ID of the station
                BufferedReader reader = new BufferedReader(new FileReader("aggregationDatabase.txt"));
                String line;
                Boolean valid_data = false;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("{")) { // if ID is detected
                        valid_data = true; // mark to append data
                    } else if (line.contains("}")) {
                        valid_data = false;
                        if (selected_data.toString().contains("id:" + id)) {
                            return selected_data.toString(); // if id is detected within data, return this data
                        }
                        selected_data.setLength(0); // Otherwise if string is not detected, reset stringbuilder
                    } else if (valid_data == true) {
                        selected_data.append(line + "\r\n"); // otherwise, continue appending data until encounters next
                                                             // bracket
                    }
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null; // No data found
    }

}
