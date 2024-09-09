package server;

import java.net.*;
import java.io.*;

public class TFTP_TCP_Server {
    public static DatagramSocket socket; /** main port for the server*/

    /**
     * This is the main method which allows the,
     * run method to be invoked
     * @param args None
     */
    public static void main(String[] args) {
        System.out.println("Server waiting for a packet...");
        runServer();
    }

    /**
     * 249763
     * This method allows the server to enter a infinite loop,
     * and wait for a TFTP request to be received, new threads are used,
     * to handle the request
     */
    public static void runServer()  {
        try {
            // For TFTP packets
            socket = new DatagramSocket(69);
            System.out.println("Server waiting for a packet...");

            // This open the server and stay on until it receives a packet
            while (true) {
                byte[] recBuf = new byte[1000];
                DatagramPacket recPack = new DatagramPacket(recBuf, recBuf.length);
                socket.receive(recPack);

            }
        } catch (IOException error) {
            System.out.println("Error! Shutting Server Down...");
        }
        socket.close();
    }
}
