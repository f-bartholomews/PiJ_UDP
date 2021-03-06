package main;

import java.io.IOException;
import java.util.List;

/**
 * Class which deals with UDP Connection with a Client (identified as a {@see Connection} instance).
 * Its Runnable thread should be launched by the main {@see Server}.
 *
 * @author federico.bartolomei (BBK-PiJ-2014-21)
 */
public interface UDPServer extends Runnable {
    /**
     * Periodically check the Server's list of active Connection. If at least one RECEIVER client is in the list,
     * it will start multicasting the audio data which should be already being streamed by the SENDER client.
     *
     */
    @Override
    public void run();

    /**
     * Send a UDP request to the Client identified by the Connection taken as parameter, start receiving
     * chunks of audio data from it and store it to be used by the multicasting routine.
     * If the SENDER Client disconnects during the streaming, its Connection should be removed from the
     * Server's list of Connections and the next oldest active Client should be promoted to new SENDER.
     *
     * @param connection the Connection with the SENDER Client
     * @throws IOException for an error during connection. If the error happens during the UDP transmission
     * the exception should be caught and recovered with the deletion of the old and selection of a new SENDER.
     * The exception will be thrown only if an IO Error occurs during this second recovery phase.
     */
    public void getSenderAudio(Connection connection) throws IOException;

    /**
     * Send packets of data (retrieved by the byte[] given as parameter) via multicast to any RECEIVER Client
     * connected to its InetAddress group and port.
     *
     * @param data the data to be sent via multicasting
     * @throws IOException for an error during connection.
     */
    public void multicastAudio(byte[] data) throws IOException;

    /**
     * Getter for the list of active {@see Connections}. It might be empty.
     * Each Connection in the list wraps a Socket connected with a specific Client,
     * that Client's unique ID and {@see ClientStatus} of SENDER or RECEIVER.
     *
     * @return a List, maybe empty, of active Connections.
     */
    public List<Connection> getList();

}
