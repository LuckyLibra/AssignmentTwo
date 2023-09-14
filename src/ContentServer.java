import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

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
        StringBuilder file_content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String new_line;
            file_content.append("{");
            file_content.append("\n");

            while ((new_line = reader.readLine()) != null) { // while file not empty add content
                file_content.append(new_line);
                file_content.append("\n");
            }

            file_content.append("}");
            file_content.append("\n");
        } catch (IOException e){
            e.printStackTrace();
        }
        return file_content.toString(); // Return file content as string
    }

    private static void sendRequest(String file, String server, int clock) {
        // Call readFile(file) here to get data
        String json_contents = readFile(file);

        try {
            String host_name = server.split(":")[0];
            int port_num = Integer.parseInt(server.split(":")[1]);
            Socket socket = new Socket(host_name, port_num); // Replace this with input from args

            // send data here
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            output.println(
                    "PUT /weather.json \n" +
                            "User-Agent: ATOMClient/1/0 \n" +
                            "Content-Type: application/json \n" +
                            "Content-Length: " + json_contents.length() + "\n" +
                            json_contents);

            socket.close(); // Close socket
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
