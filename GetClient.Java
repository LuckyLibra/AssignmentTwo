import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GetClient {
    public static void main(String[] args) {
        // lamport clock
        String server_name = args[0]; // "http://servername.domain.domain:portnumber", "http://servername:portnumber",
                                      // "servername:portnumber"
        int lamport_clock = 0; // lamport clock initialised at 0 to begin
        String station_id = null;

        if (args.length > 1) {
            station_id = args[1]; // if station ID exist, apply it, else null
        }

        sendRequest(server_name, station_id);
    }

    public static void sendRequest(String server_name, String station_id) {
        String host_name = server_name.split(":")[0];
        int port_num = Integer.parseInt(server_name.split(":")[1]);
        try (
                Socket socket = new Socket(host_name, port_num); // Use arg inputs to determine aggregation server
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true); // output to write
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Input to
                                                                                                           // read
        ) {
            System.out.println("Socket is running, seeking to connect to " + host_name + " " + port_num);

            if (station_id != null) { // if station ID was used as an argument
                output.println("GET /weather.json/" + station_id + " HTTP/1.1\r\n" +"User-Agent: ATOMClient/1/0\r\n" +
                                "Content-Type: application/json\r\n");
            } else { // If no station ID retrieve all json data
                output.println("GET /weather.json HTTP/1.1\r\n" + "User-Agent: ATOMClient/1/0\r\n" + "Content-Type: application/json\r\n");
            }
            System.out.println("GET request sent to server! ");

            String responseLine;
            while ((responseLine = input.readLine()) != null) {
                System.out.println(responseLine);
            }

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e){
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}