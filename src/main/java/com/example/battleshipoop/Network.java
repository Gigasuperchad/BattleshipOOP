package com.example.battleshipoop;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Network {
    public static String getLocalIPAddress() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

        while (interfaces.hasMoreElements()) {
            NetworkInterface iface = interfaces.nextElement();

            // Пропускаем отключенные и loopback интерфейсы
            if (!iface.isUp() || iface.isLoopback() || iface.isVirtual()) {
                continue;
            }

            Enumeration<InetAddress> addresses = iface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();

                // Возвращаем первый IPv4 адрес
                if (addr.getAddress().length == 4) { // IPv4
                    return addr.getHostAddress();
                }
            }
        }

        throw new RuntimeException("No network adapters with an IPv4 address in the system!");
    }

    public static String getLocalHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "Unknown";
        }
    }
}