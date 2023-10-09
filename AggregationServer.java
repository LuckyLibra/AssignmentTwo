import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.regex.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.List;

public class AggregationServer {
    public static List<Integer> timers = new ArrayList<>();

    static {
        timers.add(0); // Initialize the list with an initial value of 0
    }

    public static final Object lock = new Object(); // prepare lock
    public static void main(String[] args) {
        int port = 4567; // default number

        if (args.length > 0) { // If a port number was in the command line
            port = Integer.parseInt(args[0]); // Update port number to command line arg
        }

        try {
            System.out.print("Aggregation Server is running at " + port + "\n");
            ServerSocket serverSocket = new ServerSocket(port); // open socket

            while (true) {
                Socket client_socket = serverSocket.accept(); // wait for connections
                HandleRequest handleRequest = new HandleRequest(client_socket); // Use class to create a
                                                                                               // new thread so
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

            int current_time = LamportClock.getCurrentTime();
            LamportClock.updateTime(current_time);
            current_time = LamportClock.getCurrentTime();
            handleTime(request_from_file, current_time);

            if (total_message.contains("PUT")) { // Call put function for put request from contentserver
                System.out.println("PUT request received");
                putResponse(write, request_from_file, current_time); //call put procedures

            } else if (total_message.contains("GET")) { // call get for request from client

                getResponse(write, request_from_file);
            } else {
                write.println("HTTP/1.1 400"); // ERROR
            }

            updateTimers(current_time);

            Thread.sleep(30000); // Sleep for 30 seconds
            Boolean check_alive = checkAlive(write, read_file); // check if content server is still alive
            if (check_alive == false) { // the content server has not responded
                String request_id = findRequestID(request_from_file);
                removeFromDatabase(request_id);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Determines what must occur after a put response has been called
    public void putResponse(PrintWriter write, StringBuilder read, int time) { // takes read and write
        // Check if there is an Aggregation Database if not create the file
        File database = new File("aggregationDatabase.txt");

        if (database.exists()) { // first check if file exists
            if (database.length() == 0) { // If file exists but has no content
                boolean result = insertIntoFile(read); // Insert json data into file

                if (result == true) {
                    write.println("HTTP/1.1 200 OK "); // Sends 200 OK Response to content server
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
                        removeFromDatabase(request_id); // ID to remove from database
                        insertIntoFile(read);
                        write.println("HTTP/1.1 200 OK "); // Sends 200 OK Response to content server

                    } else {
                        insertIntoFile(read); // If ID not present, add to database
                        write.println("HTTP/1.1 200 OK "); // Sends 200 OK Response to content server
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
        }
    }

    // Determines the output that should follow after a get response has been sent
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
        if (database.length() > 0) {
            write.println(database);
        } else {
            write.println("The database currently has no data available.");
        }

        try {
            this.client_socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // sends a get request that checks if the server after waiting 30 seconds
    // sends a get request to the content server adn the content server can return a
    // body containing the message "I am alive"
    public boolean checkAlive(PrintWriter write, BufferedReader output) {
        try {
            System.out.println("Contacting content server to check if alive");
            write.println("GET /alive HTTP/1.1 \r\n" + // Sends a message to the content server
                    "User-Agent: ATOMClient/1/0 \r\n" +
                    "Content-Type: application/json \r\n");

            String incoming_message; // Read

            Timer timer = new Timer(); // create a new timer to handle no message timeout
            timer.schedule(new TimerTask() { // Create a timer task which will end after 5 seconds
                public void run() {
                    System.out.println(
                            "Server has not responded to GET request. Closing connection and removing old data.");
                    timer.cancel();
                }
            }, 10000); // if after 5 seconds there is no response. Call timer

            while ((incoming_message = output.readLine()) != null) { // read outputs from content server
                timer.cancel(); // if a message is receive cancel the timer
                System.out.println(incoming_message);
                return true; // return true if message received
            }
        } catch (IOException e) {
            e.printStackTrace(write);
        }
        return false;
    }

    // creates databaseAggregation.txt file
    public void createFile() {
        File new_database = new File("aggregationDatabase.txt");

        try {
            new_database.createNewFile(); // Create new file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Inserts data into databaseAggregation.txt file
    public boolean insertIntoFile(StringBuilder read) {
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

    // This function locates the ID value of a json object which has been converted
    // to a string
    public String findRequestID(StringBuilder read) {
        Pattern regex = Pattern.compile("id:" + "([^\\n]+)");
        Matcher id_value = regex.matcher(read);

        if (id_value.find()) {
            return id_value.group(1).trim();
        }
        return "";
    }

    // When testing lamport, we make it so that any thread from Sydney with the
    // weather as 'sunny' will take longer to process
    // This detects if we are testing lamport by determining if the sunny thread
    // exists
    public String detectLamportTest(StringBuilder read) {
        Pattern regex = Pattern.compile("cloud:" + "([^\\n]+)");
        Matcher id_value = regex.matcher(read);

        if (id_value.find()) {
            return id_value.group(1).trim();
        }
        return "";
    }

    // Used to remove data from the database based on the string provided, this can
    // be used when removing data with duplicate id's, as well as removing data from
    // a weather station
    // where the connection has been lost
    public void removeFromDatabase(String ID_to_remove) {
        try {
            StringBuilder all_data = new StringBuilder();
            StringBuilder selected_data = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader("aggregationDatabase.txt"));
            String line;
            boolean valid_data = false;
            while ((line = reader.readLine()) != null) { // Essentially follows the same formaula as the retrieve
                                                         // database logic, where it searches for the presence of id
                                                         // within the brackets then determines if valid or not
                if (line.contains("{")) {
                    valid_data = true;
                    selected_data.append(line).append("\n"); // Include the opening bracket in the result
                } else if (line.contains("}")) {
                    valid_data = false;
                    if (!selected_data.toString().contains("id:" + ID_to_remove)) {
                        selected_data.append(line).append("\n"); // Include the closing bracket in the result
                        all_data.append(selected_data);
                    }
                } else if (valid_data) {
                    selected_data.append(line).append("\n");
                }
            }

            if (selected_data.toString().contains("id:" + ID_to_remove)) {
                BufferedWriter writer = new BufferedWriter(new FileWriter("aggregationDatabase.txt"));
                writer.write(""); // Empty the file by writing and empty string
                writer.close();

            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // This function takes a string id input an uses it to retrieve database
    // information for a get request
    // If the input is 'null', it will retrieve all data, if it is not, it will only
    // retrieve data for that ID
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

    // Adds the lamport timer of the current thread to an array to signify that it
    // has completed its task
    public void updateTimers(int time) {
        synchronized (AggregationServer.lock) { // Use the lock object
            AggregationServer.timers.add(time); // append to end of list
            AggregationServer.lock.notifyAll(); // Notify all waiting threads
        }
    }

    // Handle time is a function which delays a thread until it can continue in the
    // correct order,
    // that is, the previous threads have completed and it obeys the laws of lamport
    // clocks
    public void handleTime(StringBuilder read, int currentTime) {
        String detect_lamport = findRequestID(read); // Determine if we need to add wait time if we're testing lamport
        String detect_weather = detectLamportTest(read);

        try {
            if (detect_lamport.contains("IDS9999") & detect_weather.contains("Sunny")) { // Used for testing lamport
                                                                                         // clocks to simulate increased
                                                                                         // wait time
                Thread.sleep(7000); // Sleep for 15 seconds, simulates increased wait time for Sydney weather
                                    // station
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        synchronized (AggregationServer.lock) {
            // If the list does not contain the previous thread clock, it means that it has
            // not complete its task
            while (!AggregationServer.timers.contains(currentTime - 1)) {
                try {
                    AggregationServer.lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return; // Continue with the rest of the code
        }
    }

}
