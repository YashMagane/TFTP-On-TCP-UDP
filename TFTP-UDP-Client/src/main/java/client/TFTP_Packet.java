package client;

import java.io.*;
import java.net.*;

import static client.TFTP_UDP_Client.serverAddress;
import static client.TFTP_UDP_Client.socket;

/**
 * 249763
 * This Class contains a few methods which assist
 * the production of the TFTP packets, for the read and,
 * write requests
 */

public class TFTP_Packet {

    /**
     * This is the method which I want to use to make,
     * the data packet
     * @param readData Data read
     * @param readCount amount of data
     * @param blockNumber number of block
     * @return Data packet
     */
    public static DatagramPacket dataPacket(char[] readData, int readCount, int blockNumber) {
        //Default data block as per RFC 1350
        int dataSize = 512;
        byte[] dataBuffer = new byte[dataSize];
        if (readCount < dataSize) {
            dataBuffer = new byte[readCount];
        }
        for (int i = 0; i < readCount; i++) {
            dataBuffer[i] = (byte) readData[i];
        }
        byte[] dataOp = opcodeProduction(Opcode_RFC1350.DATA); //getting op code
        byte[] byteBlock = IntToByte(blockNumber); //converting block number to byte for concatenation
        assert false;
        byte[] op_block = concatArrays(dataOp, byteBlock);
        dataBuffer = concatArrays(op_block, dataBuffer);
        return new DatagramPacket(dataBuffer, dataBuffer.length);
    }

    /**
     * From RFC 1350, codes for each request
     * @param opcode number assigned to the request
     * @return opcode of request
     */
    public static byte[] opcodeProduction (Opcode_RFC1350 opcode) {
        return null; //I was trying to make the codes in byte types
    }

    /**
     * Using UDP protocol to send a achnowledgment packet to server,
     * from client
     * @param blockNumber block number of data
     * @param serverPort port for the server
     * @throws IOException for I/O errors
     */
    public static void sendACK(int blockNumber, int serverPort) throws IOException {
        byte[] byteBlock = IntToByte(blockNumber);
        byte[] pack = concatArrays(opcodeProduction(Opcode_RFC1350.ACK), byteBlock);
        DatagramPacket sendingACK = new DatagramPacket(pack, pack.length);
        udp(sendingACK, serverPort, serverAddress);
    }
    //This method is a fail, it was meant to help with making a oacket which allows the user to send their file with the correct opcode
    public static DatagramPacket reqPackets(Opcode_RFC1350 opcode, String filename) {
        byte[] byteOpcode = new byte[0];
        switch (opcode) {
            case RRQ:
                byteOpcode = opcodeProduction(Opcode_RFC1350.RRQ);
                break;
            case WRQ:
                byteOpcode = opcodeProduction(Opcode_RFC1350.WRQ);
                break;
            default:
                socket.close();
                //return new DatagramPacket(byteOpcode, filename);
        }
        return null;
    }

    /**
     * This method sends a write request packet to the server using the filename,
     * it uses a request packet method which generates the packet with the opcode,
     * This packet is then transferred by a UDP connection
     * @param filename name of file
     * @return server port
     * @throws IOException for I/O errors
     */
    public static int sendWRQ(String filename) throws IOException {
        DatagramPacket wrqPack = reqPackets(Opcode_RFC1350.WRQ, filename);
        int serverPort;
        int timeout = 1000; //1000ms or 1s timeout
        socket.setSoTimeout(timeout);
        while (true) {
            int defServerPort = 69;
            assert wrqPack != null;
            udp(wrqPack, defServerPort, serverAddress);
            DatagramPacket ackObt = ackObtained(0, wrqPack, socket, serverAddress);
            serverPort = (ackObt).getPort();
            return serverPort;
        }
    }

    /**
     * UDP transmission, method makes a packet for UDP
     * @param packet data of the pack
     * @param port port number
     * @param address IP address
     * @return a UDP data packet
     */
    public static DatagramPacket udp(DatagramPacket packet, int port, InetAddress address) {
        return new DatagramPacket(packet.getData(), packet.getLength(), address, port);
    }

    /**
     * This method allows the client to receive the acknowledgment sent by the server,
     * it also checks if it's the right acknowledgment for the packet it was sent.
     * @param expACK expected acknowledgment
     * @param nextPack next packet
     * @param socket socket
     * @param serverAddress IP address
     * @return acknowledgment packet
     * @throws IOException for I/O errors
     */
    public static DatagramPacket ackObtained(int expACK, DatagramPacket nextPack, DatagramSocket socket, InetAddress serverAddress) throws IOException {
        byte[] ackData = new byte[4];
        DatagramPacket ackPacket = new DatagramPacket(ackData, ackData.length);
        socket.receive(ackPacket); //extracting ack number from pack
        int ackReceived = ((ackData[2] & 0xff) << 8) | (ackData[3] & 0xff);
        if (ackReceived == expACK) { //comparing to check if its the correct number
            return ackPacket;
        } else {
            udp(nextPack, ackPacket.getPort(), serverAddress);
            return null;
        }
    }

    /**
     * converting int types into byte types
     * @param i any integer
     * @return byte type
     */
    public static byte[] IntToByte(int i) {
        byte b = (byte)i; //casting
        return new byte[]{b};
    }

    /**
     * Converting byte types into int
     * @param b Byte value
     * @return int value
     */
    public static int ByteToInt(byte[] b) {
        int i = 1;
        return (b[i]);
    }

    /**
     * Adding two arrays together
     * @param i first array
     * @param j second array
     * @return first and second array combined
     */
    public static byte[] concatArrays(byte[] i, byte[] j) {
        int a = i.length;
        int b = j.length;
        return new byte[a+b];
    }



}
