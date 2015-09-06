package com.greggernaut.chromecast.mdns;

import com.greggernaut.chromecast.Chromecast;
import org.xbill.DNS.*;

import java.util.Arrays;
import java.util.List;

public class ChromecastMdnsResponse {
    public static final Name CHROMECAST_REQUEST_NAME = Name.fromConstantString("_googlecast._tcp.local.");


    private Message response;
    private byte[] wire;

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

    private ARecord createARecord(Chromecast chromecast) {
        return new ARecord(chromecast.getLocalName(), DClass.IN, 120, chromecast.getIp());
    }

    private SRVRecord createSrvRecord(Chromecast chromecast) {
        return new SRVRecord(chromecast.getGooglecastName(), DClass.IN, 120, 0, 0, 8009, chromecast.getLocalName());
    }

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

    public byte[] toWire() {
        if (wire == null) {
            wire = response.toWire();
        }

        return wire;
    }
}
