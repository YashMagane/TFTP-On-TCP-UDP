package client;

import java.io.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Scanner;

import static client.TFTP_Packet.*;

/**
 * 249763
 * This is my implementation of the TFTP protocol on top of UDP
 * This is the Client which is able to read and write a file
 */
public class TFTP_UDP_Client {
    public static final int dataSize = 512;//Default data block as per RFC 1350
    public static InetAddress serverAddress; //IP for the server
    public static final Scanner Scan = new Scanner(System.in); //To scan what the user typed
    public static final int minPort = 1024; //Minimum port a client can take
    public static DatagramSocket socket; //Clients socket

    /**
     * This is the main method, run it to start the client
      * @param args none
     * @throws IOException for I/O errors
     */
    public static void main(String[] args) throws IOException {
        System.out.println("\nClient is Ready");
        run();
        System.out.println("Client is now turned off.");
    }

    /**
     * This method carries on after the main
     * @throws IOException for I/O errors
     */
    private static void run() throws IOException {
        System.out.println();
        System.out.print("Type 1 to write a file, or type 2 for requesting files from the server: ");
        String userResponse = Scan.nextLine();
        System.out.println();

        if (userResponse.equals("1")) {
            writeReq();
        } else if (userResponse.equals("2")) {
            readReq();
        } else {
            System.err.println("Type 1 or 2");
            socket.close();
            System.exit(-1);
        }
        socket.close();
    }

    /**
     * This is the write method, first it extracts the user's input on the server IP address,
     * and creates a new socket with the port number they provided,
     * as this is the write method for the Client, it allows user,
     * to write files to server. First it gives the file name and sends,
     * data packets to the server via a UDP connection,
     * an acknowledgment is then sent back allowing more data to be sent.
     * @throws IOException for I/O errors
     */

    private static void writeReq() throws IOException {
        //Getting IP Address
        System.out.print("Type in the server's IP Address");
        String addressTyped = Scan.nextLine();
        System.out.println();

        serverAddress = InetAddress.getByName(addressTyped);

        System.out.print("Type client's port number (above 1024)");
        int userPort = Integer.parseInt(Scan.nextLine());
        if (userPort < minPort) {
            System.out.println("Client port number provided is not above 1024");
            System.exit(-1);
        } else {
            socket = new DatagramSocket(userPort);
        }
        //WRITE REQUEST
        System.out.println("Write request will now begin");
        System.out.println();
        int blockNumber = 0;
        int expAck = blockNumber;
        int serverPort;
        DatagramPacket nextPack;
        FileReader file;

        //Getting file name
        System.out.print("Type the filename which you want to write: ");
        String filename = Scan.nextLine();

        //Checking if file name can be found
        try {
            file = new FileReader(filename);
        } catch (FileNotFoundException foe) {
            System.out.println("File not found!");
            return;
        }
        //Write request sent via UDP connection
        serverPort = sendWRQ(filename);
        blockNumber++;
        //reads content
        BufferedReader reader = new BufferedReader(file);
        //as of RFC 1350, 512 bytes are read
        char[] readData = new char[dataSize];
        int numberOfRead = 0;

        while (true) {
            try {
                nextPack = dataPacket(readData, numberOfRead, blockNumber); //create a data packet
                udp(nextPack, serverPort, serverAddress);//send data packet
                byte[] receivedAck = (ackObtained(expAck, nextPack, socket, serverAddress)).getData(); //ACK for data packets sent
                int blockReceived = ByteToInt(new byte[]{receivedAck[2], receivedAck[3]});
                if (blockReceived == expAck) {
                    blockNumber++;
                    expAck++;
                    break;
                }
            } catch (SocketTimeoutException i){
                break; //for acks not coming in
            }
        }
        reader.close();
        file.close();
        //unable to get retransmissions coded :(
    }

    /**
     * This is the read method for the client, this method allows data,
     * packets to be received from the server, acknowledgments are sent for,
     * each data pack received.
     * @throws IOException for I/O errors
     */
    private static void readReq() throws IOException {
        //Getting IP Address
        System.out.print("Type in the server's IP Address");
        String addressTyped = Scan.nextLine();
        System.out.println();

        serverAddress = InetAddress.getByName(addressTyped);

        System.out.print("Type client's port number (above 1024)");
        int userPort = Integer.parseInt(Scan.nextLine());
        if (userPort < minPort) {
            System.out.println("Client port number provided is not above 1024");
            System.exit(-1);
        } else {
            socket = new DatagramSocket(userPort);
        }

        System.out.println("Write request will now begin");
        System.out.println();
        int blockExpected = 0;
        int blockReceived = 0;
        int useCount = 0;
        String filename;
        StringBuilder fileContent = new StringBuilder(dataSize);
        byte[] totalSize = new byte[dataSize];

        System.out.print("Type the filename which you want to read: "); //gets the filename from the user
        filename = Scan.nextLine();
        System.out.println();

        DatagramPacket rrqPack = reqPackets(Opcode_RFC1350.RRQ, filename); //making a read request packet
        DatagramPacket received = new DatagramPacket(totalSize, totalSize.length);

        //1 second before a timeout exception is thrown
        int timeout = 1000;
        socket.setSoTimeout(timeout);
        while (true) {
            try {
                //Server Port
                int defServerPort = 69;
                assert rrqPack != null;
                udp(rrqPack, defServerPort, serverAddress); //sending read request to server via UDP
                socket.receive(received); //acknowledgment for receiving data pack
                break;
            } catch (SocketTimeoutException soe) {
                //Times a client can attempt to resend a packet
                int maxAttempts = 50;
                if (useCount > maxAttempts) {
                    System.out.println("Server Inactive, Closing connection..");
                    return;
                }
                System.out.println("Timeout");
            }
            useCount++;
        }

        int serverPort = received.getPort();
        socket.connect(received.getAddress(), serverPort);

        //Sending rest of data packets, while sending the correct acknowledgment
        while (true) {
            if (blockReceived == blockExpected) {
                sendACK(blockReceived, serverPort);
                String dataReceived = new String(received.getData(), 0, received.getLength());
                fileContent.append(dataReceived.substring(4));
                blockExpected++;
            } else {
                System.out.println("Block received not as expected number. Duplicate.");
                sendACK(blockReceived, serverPort);
            }
        }
    }
}