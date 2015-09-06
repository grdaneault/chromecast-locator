package com.greggernaut.chromecast;

import com.greggernaut.chromecast.mdns.MdnsListener;
import com.greggernaut.chromecast.ssdp.SsdpListener;

import java.util.ArrayList;
import java.util.List;

public class ChromecastLocator {
    public static void main(String[] args) throws InterruptedException {
        List<Chromecast> chromecasts = new ArrayList<>(1);
        chromecasts.add(ChromecastDiscovery.discover("172.16.0.120"));

        Thread mdns = new Thread(new MdnsListener(chromecasts));
        mdns.start();

        Thread ssdp = new Thread(new SsdpListener(chromecasts));
        ssdp.start();


        mdns.join();
        ssdp.join();
    }
}
