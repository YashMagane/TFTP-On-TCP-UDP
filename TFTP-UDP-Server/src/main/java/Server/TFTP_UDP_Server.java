package Server;

import java.io.*;
import java.net.*;

/**
 * 249763
 * This is the TFTP UDP Server, which waits and listens on,
 * an infinite loop, for requests on port 69.
 *
 */
public class TFTP_UDP_Server {

    private static final int dataSize = 512; /** size of data block as 512 bytes*/
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
                byte[] recBuf = new byte[dataSize];
                DatagramPacket recPack = new DatagramPacket(recBuf, recBuf.length);
                socket.receive(recPack);

                // Start a new thread to handle the TFTP request
                //TFTPRequestHandler requestHandler = new TFTPRequestHandler(recPack);
                //Thread thread = new Thread((Runnable) requestHandler);
               //thread.start();
            }
        } catch (IOException error) {
            System.out.println("Error! Shutting Server Down...");
        }
        socket.close();
    }
}