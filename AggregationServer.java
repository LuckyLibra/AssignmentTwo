package AssignmentTwo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class AggregationServer {
    public static void main(String [] args){
        int port = 4567; //default number
        int lamport_clock = 0; //lamport clock

        if ( args.length > 0 ){ //If a port number was in the command line
            port = Integer.parseInt(args[0]); //Update port number to command line arg
        }

        try {
            ServerSocket serverSocket = new serverSocket(port); //open socket

            while (true) {
                Socket.clientSocket = serverSocket.accept(); // wait for connections

                

            }
        }



    }
}
