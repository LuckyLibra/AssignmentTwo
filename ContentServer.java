import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
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
            file_content.append("\r\n");

            while ((new_line = reader.readLine()) != null) { // while file not empty add content
                file_content.append(" " + new_line);
                file_content.append("\r\n");
            }

            file_content.append("}");
            file_content.append("\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file_content.toString(); // Return file content as string
    }

    private static void sendRequest(String file, String server, int clock) {
        // Call readFile(file) here to get data
        String json_contents = readFile(file);

        try (Socket socket = new Socket()) {

            String host_name = server.split(":")[0];
            int port_num = Integer.parseInt(server.split(":")[1]);
            socket.connect(new InetSocketAddress(host_name, port_num));// Replace this with input from args

            System.out.println("Socket is running, seeking to connect to " + host_name + " " + port_num);

            // send data here
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            output.println( "PUT /weather.json HTTP/1.1\r\n" + "User-Agent: ATOMClient/1/0\r\n" + "Content-Type: application/json\r\n" +
                            "Content-Length: " + json_contents.length() + "\r\n " + json_contents);

            System.out.println("Message sent! ");

            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String responseLine;
            while ((responseLine = input.readLine()) != null) {
                System.out.println(responseLine + "\r\n");
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
