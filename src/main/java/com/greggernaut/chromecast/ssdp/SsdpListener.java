package com.greggernaut.chromecast.ssdp;

import com.google.common.base.Preconditions;
import com.greggernaut.chromecast.Chromecast;

import java.io.IOException;
import java.net.*;
import java.util.List;

/**
 * Created by gregd on 9/6/2015.
 */
public class SsdpListener implements Runnable {


    private static final String SSDP_GROUP = "239.255.255.250";
    private static final int SSDP_PORT = 1900;
    private static final int BUFFER_SIZE = 2048;

    private List<Chromecast> chromecasts;
    private MulticastSocket socket;
    private DatagramSocket unicast;
    private DatagramPacket datagram;

    public SsdpListener(List<Chromecast> chromecasts) {
        this.chromecasts = Preconditions.checkNotNull(chromecasts);
        byte[] buffer = new byte[BUFFER_SIZE];
        this.datagram = new DatagramPacket(buffer, buffer.length);
        try {
            this.socket = new MulticastSocket(SSDP_PORT);
            this.socket.joinGroup(InetAddress.getByName(SSDP_GROUP));

            this.unicast = new DatagramSocket(new InetSocketAddress(SSDP_PORT));
        } catch (IOException e) {
            e.printStackTrace();
            this.socket = null;
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                socket.receive(datagram);
                SsdpRequest request = new SsdpRequest(datagram.getData());

                if (request.isSearch()) {
                    for (Chromecast chromecast : chromecasts) {
                        if (request.matches(chromecast)) {
                            SsdpResponse response = new SsdpResponse(request, chromecast);
                            byte[] payload = response.toWire();
                            InetSocketAddress target = new InetSocketAddress(datagram.getAddress(), datagram.getPort());
                            DatagramPacket responsePacket = new DatagramPacket(payload, payload.length, target);
                            socket.send(responsePacket);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
