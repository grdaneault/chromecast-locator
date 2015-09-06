# Chromecast Locator
This is a simple program that enables you to use a chromecast that is not on your local subnet.

## How it works
Chromecasts are discovered using either MDNS or SSDP.  These protocols generally 
are not (and should not be) routed, meaning that in order for the device to be 
discovered, it must be on located on the same subnet.  Unfortunately, chromecasts 
cannot be accessed directly by IP, even if that IP is known and can be accessed 
from the current subnet.

This program masquerades as a chromecast and provides the necessary discovery 
responses for the remote chromecast.  When MDNS and SSDP requests are received, 
the program informs the requester that a compatible device can be found at the 
device's actual IP, thereby allowing chromecasts to operate across the bounds 
of their local subnet.  Once the connection has been established, the chromecast 
takes over and operates as normal.
