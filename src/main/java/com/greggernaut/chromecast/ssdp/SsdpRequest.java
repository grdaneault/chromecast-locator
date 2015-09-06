package com.greggernaut.chromecast.ssdp;

import com.greggernaut.chromecast.Chromecast;

import java.util.HashMap;
import java.util.Map;

public class SsdpRequest {
    Map<String, String> headers;

    public String packetHeader;

    private static final String MSEARCH_HEADER = "M-SEARCH * HTTP/1.1";

    public SsdpRequest(byte[] data) {
        String request = new String(data);
        headers = new HashMap<>();
        String[] lines = request.split("[\r\n]+");
        packetHeader = lines[0];
        for (int i = 1; i < lines.length; i++) {
            if (lines[i].trim().length() > 0 && lines[i].indexOf(':') >= 0) {
                String[] header = lines[i].split(": ?", 2);
                headers.put(header[0], header[1]);
            }
        }
    }

    public boolean isSearch() {
        return MSEARCH_HEADER.equals(packetHeader);
    }

    public String getHeader(String header) {
        return headers.get(header);
    }

    public boolean matches(Chromecast chromecast) {
        String query = getHeader("ST");
        return query.equalsIgnoreCase("urn:dial-multiscreen-org:service:dial:1") ||
                query.equalsIgnoreCase("urn:dial-multiscreen-org:device:dial:1") ||
                query.equalsIgnoreCase("ssdp:all") ||
                query.equalsIgnoreCase("upnp:rootdevice") ||
                query.equalsIgnoreCase("uuid:" + chromecast.getUuid());
    }
}
