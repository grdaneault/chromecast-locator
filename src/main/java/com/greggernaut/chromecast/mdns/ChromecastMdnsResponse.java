package com.greggernaut.chromecast.mdns;

import com.greggernaut.chromecast.Chromecast;
import org.xbill.DNS.*;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a response to an MDNS query for a chromecast
 *
 * Based on the information here:
 * https://github.com/jloutsenhizer/CR-Cast/wiki/Chromecast-Implementation-Documentation-WIP#mdns
 */
public class ChromecastMdnsResponse {
    public static final Name CHROMECAST_REQUEST_NAME = Name.fromConstantString("_googlecast._tcp.local.");

    /**
     * The raw outgoing DNS message
     */
    private Message response;

    /**
     * The byte array representation of the message that can be sent over the wire
     */
    private byte[] wire;

    /**
     * Constructor.
     *
     * @param chromecast The chromecast that matches the MDNS request
     */
    public ChromecastMdnsResponse(Chromecast chromecast) {

        response = new Message();

        Header header = new Header();
        header.setFlag(Flags.AA); // Authoritative answer
        header.setFlag(Flags.QR); // Query response

        response.setHeader(header);

        // Primary pointer from the search domain to our specific chromecast
        PTRRecord ptr = new PTRRecord(CHROMECAST_REQUEST_NAME, DClass.IN, 120, chromecast.getGooglecastName());
        response.addRecord(ptr, Section.ANSWER);


        TXTRecord txt = createTxtRecord(chromecast);
        SRVRecord srv = createSrvRecord(chromecast);
        ARecord a = createARecord(chromecast);

        response.addRecord(txt, Section.ADDITIONAL);
        response.addRecord(srv, Section.ADDITIONAL);
        response.addRecord(a, Section.ADDITIONAL);
    }

    /**
     * Creates the A record for the chromecast
     *
     * @param chromecast The chromecast to link
     * @return The A record
     */
    private ARecord createARecord(Chromecast chromecast) {
        return new ARecord(chromecast.getLocalName(), DClass.IN, 120, chromecast.getIp());
    }

    /**
     * Creates a service locator record for the chromecast with the appropriate port and hostname
     *
     * @param chromecast The chromecast to link
     * @return The SRV record
     */
    private SRVRecord createSrvRecord(Chromecast chromecast) {
        return new SRVRecord(chromecast.getGooglecastName(), DClass.IN, 120, 0, 0, 8009, chromecast.getLocalName());
    }

    /**
     * Creates a TXT record with additional parameters for the chromecast
     *
     * @param chromecast The chromecast to link
     * @return The TXT record
     */
    private TXTRecord createTxtRecord(Chromecast chromecast) {
        List<String> strings = Arrays.asList(
                "id=" + chromecast.getId(),
                "ve=01",
                "md=Chromecast",
                "ic=/setup/icon.png",
                "fn=" + chromecast.getName(),
                "ca=5",
                "st=0",
                "rs="
        );
        return new TXTRecord(chromecast.getGooglecastName(), DClass.IN, 4500, strings);
    }

    /**
     * The byte array that can be sent as a packet over the wire
     *
     * @return The response payload
     */
    public byte[] toWire() {
        if (wire == null) {
            wire = response.toWire();
        }

        return wire;
    }
}
