package com.greggernaut.chromecast.ssdp;

import com.greggernaut.chromecast.Chromecast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by gregd on 9/6/2015.
 */
public class SsdpResponse {

    Chromecast chromecast;
    byte[] response;

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public SsdpResponse(SsdpRequest request, Chromecast chromecast) {
        this.chromecast = chromecast;

        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String response = "HTTP/1.1 200 OK\n" +
                "CACHE-CONTROL: max-age=1800\n" +
                "DATE: " + date + "\n" +
                "EXT:\n" +
                "LOCATION: http://" + chromecast.getIp().getHostAddress() +
                ":8008/ssdp/device-desc.xml\n" +
                "OPT: \"http://schemas.upnp.org/upnp/1/0/\"; ns=01\n" +
                "01-NLS: baed804a-1dd1-11b2-8973-d7a6784427e5\n" +
                "SERVER: Linux/3.8.13, UPnP/1.0, Portable SDK for UPnP devices/1.6.18\n" +
                "X-User-Agent: redsonic\n" +
                "ST: " + request.getHeader("ST") + "\n" +
                "USN: uuid:" + chromecast.getUuid() + "::" +
                request.getHeader("ST") + "\n" +
                "BOOTID.UPNP.ORG: 7339 \n" +
                "CONFIGID.UPNP.ORG: 7339\n\n";

        this.response = response.getBytes();
    }

    public byte[] toWire() {
        return response;
    }
}
