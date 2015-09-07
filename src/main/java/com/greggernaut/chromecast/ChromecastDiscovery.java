package com.greggernaut.chromecast;

import com.google.common.base.Preconditions;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ChromecastDiscovery {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Specify at least one chromecast");
        }

        List<Chromecast> chromecasts = discover(args);
        for (Chromecast cast : chromecasts) {
            System.out.println(cast.toString());
        }
    }

    public static Chromecast discover(String host) {
        try {
            return parse(host);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Chromecast> discover(String[] hosts) {
        List<Chromecast> casts = new ArrayList<>(hosts.length);

        for (String host : hosts) {
            try {
                Chromecast chromecast = parse(host);
                if (chromecast != null) {
                    casts.add(chromecast);
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }
        }

        return casts;
    }

    private static Chromecast parse(String host) throws IOException, ParserConfigurationException, SAXException {
        String ssdpPath = "http://" + host + ":8008/ssdp/device-desc.xml";
        URL ssdpUrl = new URL(ssdpPath);
        InputStream inputStream = ssdpUrl.openStream();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document ssdp = builder.parse(inputStream);

        String name = ssdp.getElementsByTagName("friendlyName").item(0).getTextContent();
        String id = ssdp.getElementsByTagName("UDN").item(0).getTextContent().substring(5);
        String ip = ssdp.getElementsByTagName("URLBase").item(0).getTextContent().substring(7);
        ip = ip.substring(0, ip.length() - 5);

        return Chromecast.create(name, ip, id);
    }
}
