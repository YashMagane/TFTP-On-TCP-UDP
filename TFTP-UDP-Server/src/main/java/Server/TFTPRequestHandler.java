package Server;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;

import static Server.TFTP_UDP_Server.socket;

public class TFTPRequestHandler {
    /**
     * constructor for the handler
     * @param recPack recorded packet
     */
    public TFTPRequestHandler(DatagramPacket recPack) throws IOException {
        byte[] data = recPack.getData();
        int length = recPack.getLength();

        short opcode = ByteBuffer.wrap(data, 0, 2).getShort();

        if (opcode == 1) { //READ REQUEST
            byte[] dataPacket = new byte[0];
            DatagramPacket dataDatagram = new DatagramPacket(dataPacket, dataPacket.length, recPack.getAddress(), recPack.getPort());
            socket.send(dataDatagram);

        } else if (opcode == 2) { //WRITE REQUEST
            byte[] ackPacket = new byte[0];
            DatagramPacket ackDatagram = new DatagramPacket(ackPacket, ackPacket.length, recPack.getAddress(), recPack.getPort());
            socket.send(ackDatagram);
        }

    }
}
