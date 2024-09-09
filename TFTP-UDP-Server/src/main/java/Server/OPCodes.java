package Server;

/**
 * 249763
 * There are all the opcodes that TFTP uses for its packets,
 * 1 is used for the Read Request packet (RRQ)
 * 2 is used for the Write Request packet (WRQ)
 * 3 is used for Data packet (DATA)
 * 4 is used for the Acknowledgment packet (ACK)
 * 5 is used for the Error packet (ERROR)
 */
public class OPCodes {
    public static final int RRQ = 1;
    public static final int WRQ = 2;
    public static final int DATA = 3;
    public static final int ACK = 4;
    public static final int ERROR = 5;
}
