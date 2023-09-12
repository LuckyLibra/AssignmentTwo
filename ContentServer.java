package AssignmentTwo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.JSONObject;

public class ContentServer {
    public static void main(String[] args) {
        String server_name = args[0]; // "http://servername.domain.domain:portnumber", "http://servername:portnumber",
                                      // "servername:portnumber"
        String file_location = args[1]; // file location
        int lamport_clock = 0; // lamport clock initialised at 0 to begin

        sendRequest(file_location, server_name, lamport_clock);

    }

    // Takes file name as a parameter and returns the content of said file
    private static String readFile(String file) {
        JSONObject json_from_file = new JSONObject();
        BufferedReader br = new BufferedReader(new FileReader(file)); // read from file

        try {
            String line_from_file;
            while ((line = reader.readeLine()) != null) { // while each line has contents
                String[] contents = line.split(":");
                json_from_file.put(contents[0].trim(), contents[1].trim()); // Retrieves the key and value from the line
                                                                            // and puts it in object
            }
        } finally {
            br.close();
        }
        return json_from_file.toString(); // Returns json as a string
    }

    private static void sendRequest(String file, String server, int clock) {
        // Call readFile(file) here to get data
        String json_contents = readFile(file); 

        try {
            Socket socket = new Socket(123456789); //Replace this with input from args

            //send data here
            PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
            output.println(
                "PUT /weather.json \n" +
                "User-Agent: ATOMClient/1/0 \n" +
                "Content-Type: application/json" +
                "Content-Length: " + json_contents.length() + "\n" + 
                json_contents
                );

            clientSocket.close(); // Close socket
        } catch (IOException e ){
            e.printStackTrace();
        }
    }

}
