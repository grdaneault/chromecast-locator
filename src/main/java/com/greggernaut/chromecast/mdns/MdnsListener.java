package com.greggernaut.chromecast.mdns;

import com.google.common.base.Preconditions;
import com.greggernaut.chromecast.Chromecast;
import org.xbill.DNS.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.List;

public class MdnsListener implements Runnable {
    private static final int MDNS_PORT = 5353;
    private static final int BUFFER_SIZE = 2048;
    private static final String MDNS_GROUP = "224.0.0.251";

    private List<Chromecast> chromecasts;
    private DatagramPacket datagram;
    private MulticastSocket socket;

    public MdnsListener(List<Chromecast> chromecasts) {
        this.chromecasts = Preconditions.checkNotNull(chromecasts);
        byte[] buffer = new byte[BUFFER_SIZE];
        this.datagram = new DatagramPacket(buffer, buffer.length);
        try {
            this.socket = new MulticastSocket(MDNS_PORT);
            this.socket.joinGroup(InetAddress.getByName(MDNS_GROUP));
        } catch (IOException e) {
            e.printStackTrace();
            this.socket = null;
        }
    }

    public void run() {
        while (true) {
            try {
                socket.receive(datagram);
                Message input = new Message(datagram.getData());
                if (input.getQuestion() != null) {
                    if (input.getQuestion().getName().toString().equalsIgnoreCase("_googlecast._tcp.local.")) {
                        for (Chromecast chromecast : chromecasts) {
                            try {
                                byte[] resp = chromecast.getMdnsResponse().toWire();
                                DatagramPacket packet = new DatagramPacket(resp, resp.length, InetAddress.getByName(MDNS_GROUP), MDNS_PORT);
                                socket.send(packet);
                            } catch (IOException e) {
                                System.err.println("Error sending response for " + chromecast.getName());
                            }
                        }

                        System.out.println("Sent MDNS response");
                    }
                }
                datagram.setLength(BUFFER_SIZE);
            } catch (IOException e) {
                System.err.println("Error receiving datagram");
            }
        }
    }
}
