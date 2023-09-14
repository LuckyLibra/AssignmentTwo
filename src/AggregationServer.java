import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;

public class AggregationServer {
    public static void main(String[] args) {
        int port = 4567; // default number
        int lamport_clock = 0; // lamport clock

        if (args.length > 0) { // If a port number was in the command line
            port = Integer.parseInt(args[0]); // Update port number to command line arg
        }

        try {
            ServerSocket serverSocket = new ServerSocket(port); // open socket

            while (true) {
                Socket client_Socket = serverSocket.accept(); // wait for connections
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
    private Socket client_socket;

    // function call to handle get requests
    public HandleRequest(Socket client_socket) {
        this.client_socket = client_socket;
    }

    public void run() {
        try {
            BufferedReader read_file = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
            PrintWriter write = new PrintWriter(client_socket.getOutputStream(), true); // get output

            String incoming_message = read_file.readLine(); //read
            if (incoming_message.contains("PUT")) {
                putResponse(write, read_file);
            }
            else if (incoming_message.contains("GET")){
                getResponse(write, read_file);
            }
            else {
                //ERROR!
            }
            client_socket.close(); //finally close thread!
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void putResponse(PrintWriter write, BufferedReader read) { //takes read and write
        //do stuff 

    }

    public void getResponse(PrintWriter write, BufferedReader read) {
        //do stuff
    }

}
