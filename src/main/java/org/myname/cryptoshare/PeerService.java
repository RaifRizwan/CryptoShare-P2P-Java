package org.myname.cryptoshare;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import java.io.IOException;
import java.net.InetAddress;

public class PeerService {
    private JmDNS jmdns;
    // Use the same service type as your Python peer if needed.
    private final String serviceType = "_p2pfileshare._tcp.local.";
    private final String serviceName = "peer_02"; // match the Python peer name
    private final int servicePort = 9000; // match the Python port

    public void start() throws IOException {
        // Create the JmDNS instance and bind it to the local host.
        jmdns = JmDNS.create(InetAddress.getLocalHost());
        ServiceInfo serviceInfo = ServiceInfo.create(serviceType, serviceName, servicePort, "Secure P2P File Sharing Application");
        jmdns.registerService(serviceInfo);

        // Log similar messages for discovery.
        System.out.println("DEBUG - Broadcasting at " + serviceName + ":" + servicePort);
        System.out.println("DEBUG - Discovery started...");

        jmdns.addServiceListener(serviceType, new ServiceListener() {
            @Override
            public void serviceAdded(ServiceEvent event) {
                System.out.println("DEBUG - Service added: " + event.getName());
            }
            @Override
            public void serviceRemoved(ServiceEvent event) {
                System.out.println("DEBUG - Service removed: " + event.getName());
            }
            @Override
            public void serviceResolved(ServiceEvent event) {
                System.out.println("DEBUG - Service resolved: " + event.getInfo());
            }
        });

        System.out.println("DEBUG - mDNS service started as " + serviceName + " on port " + servicePort);
    }

    public void stop() throws IOException {
        if (jmdns != null) {
            jmdns.unregisterAllServices();
            jmdns.close();
        }
    }
}

