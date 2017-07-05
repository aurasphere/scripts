# WOL (Wake-On-Lan)
This folder contains an attempt to configure a Linux device to handle WOL. The project is made up by 2 files:

 - wol-packet-listener, which listens for an incoming packet on port A and sends it to wol-packet-forwarder.
 - wol-packet-forwarder, which gets a packet as input, logs it and broadcasts it over the LAN using port B.
 
With this setup you can easily expose your LAN over the Internet for WOL so please mind your own security!