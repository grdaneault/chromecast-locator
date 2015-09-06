package com.greggernaut.chromecast;

import com.google.common.base.Preconditions;
import com.greggernaut.chromecast.mdns.ChromecastMdnsResponse;
import org.xbill.DNS.Name;
import org.xbill.DNS.TextParseException;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by gregd on 9/5/2015.
 */
public class Chromecast {

    private String name;
    private InetAddress ip;
    private String uuid;

    private Name googlecastName;
    private Name localName;

    private ChromecastMdnsResponse mdns;

    private Chromecast(String name, InetAddress ip, String uuid, Name googlecastName, Name localName) {
        this.name = Preconditions.checkNotNull(name);
        this.ip = Preconditions.checkNotNull(ip);
        this.uuid = Preconditions.checkNotNull(uuid);
        this.googlecastName = Preconditions.checkNotNull(googlecastName);
        this.localName = Preconditions.checkNotNull(localName);
        this.mdns = new ChromecastMdnsResponse(this);
    }

    public static Chromecast create(String name, String ip, String uuid) {

        try {
            InetAddress ipAddr = InetAddress.getByName(Preconditions.checkNotNull(ip));
            Name google = Name.fromString(name + "._googlecast._tcp.local.");
            Name local = Name.fromString(name + ".local.");

            return new Chromecast(name, ipAddr, uuid, google, local);
        } catch (UnknownHostException e) {
            System.err.println("Bad chromecast ip: " + ip);
            return null;
        } catch (TextParseException e) {
            System.err.println("Error creating dns name: " + e.getMessage());
            return null;
        }
    }

    public Name getGooglecastName() {
        return googlecastName;
    }

    public Name getLocalName() {
        return localName;
    }

    public String getUuid() {
        return uuid;
    }

    public String getId() {
        return uuid.replace("-", "");
    }

    public String getName() {
        return name;
    }

    public InetAddress getIp() {
        return ip;
    }

    public ChromecastMdnsResponse getMdnsResponse() {
        return mdns;
    }

    public String toString() {
        return name + " (" + uuid + ")  on " + ip;
    }
}
