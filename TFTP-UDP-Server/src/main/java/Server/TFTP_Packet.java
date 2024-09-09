package Server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * 249763
 * This class is going to be producing TFTP packets based,
 * on which type of packet is being received or needs to be sent to the client
 */
public class TFTP_Packet {
    private final int opCode; // op code for requests
    private final String fileName; //name of file
    private final int blockNumber; //number of block
    private final byte[] data; //content

    public TFTP_Packet(int opcode, String file_name, int blockNumber, byte[] data) {
        this.opCode = opcode;
        this.fileName = file_name;
        this.blockNumber = blockNumber;
        this.data = data;
    }

    /**
     * This method is used to create a packet that contains,
     * data to be sent over to the client, I tried to write data into a stream,
     * which converted into a byte array
     * @param address IP address
     * @param port port number
     * @return packet
     */
    public DatagramPacket udpPackSend(InetAddress address, int port) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(output);
        try {
            dataOut.writeShort(opCode);
            if (opCode == OPCodes.DATA || opCode == OPCodes.ACK) {
                dataOut.writeShort(blockNumber);
                dataOut.write(data, 0, data.length);
            } else if (opCode == OPCodes.RRQ) {
                dataOut.writeBytes(fileName);
                dataOut.writeByte(0);
                dataOut.writeBytes("octet");
                dataOut.writeByte(0);
            }
            byte[] buf = output.toByteArray();
            return new DatagramPacket(buf, buf.length, address, port);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method takes the data from the received packet,
     * and makes necessary packet for next packet to be sent
     * @param packet packed received from client
     * @return TFTP packet
     */
    //public static TFTP_Packet udpPackReceived(DatagramPacket packet) {
        //byte[] data = packet.getData();
        //int opcode = 0;
        //int opCode = opcode;
        //if (opCode == OPCodes.RRQ) {
            ////try to use read string
       // } else if (opCode == OPCodes.DATA) {

        //} else if (opCode == OPCodes.ACK) {
            //return null;
        //}
   // }

    /**
     * This is a helper method, it reads strings,
     * from a byte array
     *
     * @param data data
     * @param pos  position
     * @return string
     */
    private static String readString(byte[] data, int pos) {
        StringBuilder sb = new StringBuilder();
        while (data[pos] != 0) {
            sb.append((char) data[pos++]);
        }
        return sb.toString();
    }
}
