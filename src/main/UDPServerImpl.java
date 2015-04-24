package main;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Implementation of {@see UDPServer}.
 *
 * @author federico.bartolomei (BBK-PiJ-2014-21)
 */
public class UDPServerImpl implements UDPServer {
    private Server server;
    private int FIVE_SECONDS = 5000;
//    private BufferedReader in = null;
//    private boolean moreData = true;
//    private File audioFile;
    private DatagramSocket socketToMulticast;
    private InetAddress group;
    private byte[] data;    // TO SORT OUT

    public UDPServerImpl(Server server) throws IOException {
        this.server = server;
        socketToMulticast = new MulticastSocket(3332);
        group = InetAddress.getByName("230.0.0.1");

     //   audioFile = file;
        /*
        try {
            in = new BufferedReader(new FileReader(audioFile));
        } catch (FileNotFoundException ex) {
            System.out.println("Cannot open file");
        }
        */
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        while (true) {
            try {
                // SYNCHRONIZED?
                while (server.getList().size() < 2 || data == null) {
                    Thread.sleep(1000);
                }
                multicastAudio(data);
            } catch (InterruptedException ex) {
                // do nothing
            } catch (IOException ex) {
                System.out.println("There has been an error while multicasting audio data");
                ex.printStackTrace();
            }
        }
    }

    //    AudioInputStream audioIn = AudioSystem.getAudioInputStream(audioFile);

    /**
     *
     *
     * @param connection
     * @throws IOException
     */
    @Override
    public void getSenderAudio(Connection connection) throws IOException {
        while (true) {
            System.out.println("Server requesting audio data from sender client " + connection.getID());
            // get a datagram socket (try-with-resources)
            try (DatagramSocket senderSocket = new DatagramSocket()) {
                InetAddress address = connection.getSocket().getInetAddress();
                // send request
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 3333);
                senderSocket.send(packet);
                System.out.println("Request sent.");
                senderSocket.setSoTimeout(FIVE_SECONDS); // 5 sec timeout before closing the connection with the client
                // get response
                packet = new DatagramPacket(buffer, buffer.length);
                // ByteArrayInputStream byteIn = new ByteArrayInputStream(received.getData());
                senderSocket.receive(packet);
                // display response
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Packet received: " + received);
                data = buffer;
            } catch (IOException ex) {
                System.out.println(connection.getID() + " (" + connection.getStatus() + ") disconnected");
                // SYNCHRONIZED
                server.getList().remove(connection);
                connection.getSocket().close();
                if (!server.getList().isEmpty()) {
                    // THIS SHOULD BE SYNCHRONIZED WITH CREATECONNECTION() IN SERVERHANDLER
                    System.out.println("Getting a new sender..");
                    Connection newSender = server.getList().get(0);
                    newSender.setStatus(ClientStatus.SENDER);
                    getSenderAudio(newSender);
                }
                // TODO let the new sender know and open a new UDP
            }
        }
    }

    /**
     * Return the packed of byte[] received from the SENDER Client via UDP;
     * that is, after the method getSenderAudio(Connection).
     *
     * @return the last packet received from the SENDER Client
     */
    public byte[] getData() {
        return data;
    }

    /**
     * {@inheritDoc}
     *
     * @param data
     * @throws java.io.IOException
     */
    @Override
    public void multicastAudio(byte[] data) throws IOException {
            /*
            System.out.println("Server will soon stream audio to receiver client " + socket.getRemoteSocketAddress());
            final int MULTICAST_PORT = 3332;
            final String GROUP_INETADDRESS = "230.0.0.1";
            */
        //       while (true) {
        try {
            //      byte[] chunk = data;
            DatagramPacket packet = new DatagramPacket(data, data.length, group, 4446);
            socketToMulticast.send(packet);
            System.out.println(packet.toString() + "sent via multicasting");
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            //
        } catch (IOException ex) {
            System.out.println("There has been an error while multicasting");
            // TODO should set a timer and catch the IOException with new getSenderAudio() ?
        }

        //    DatagramPacket packet = new DatagramPacket(buffer, buffer.length)
    }
//   }

}