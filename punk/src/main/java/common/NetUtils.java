package common;

import java.net.*;
import java.util.Enumeration;

public class NetUtils {

    public static InetSocketAddress getRemoteAddr(Socket socket) {
        return (InetSocketAddress) socket.getRemoteSocketAddress();
    }

    public static String getRemoteHost(Socket socket) {
        InetSocketAddress remoteAddr = ((InetSocketAddress) socket.getRemoteSocketAddress());
        return remoteAddr.getHostName();
    }

    public static String getLocalHost() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            String localhost = addr.getHostName();

            if (isNameResolved(addr)) {
                return localhost;
            } else {
                throw new RuntimeException(localhost + " can not be name resolved.");
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getLocalIp() throws SocketException {
        String ip = null;
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            Enumeration<InetAddress> enumIpAddr = ni.getInetAddresses();

            while (enumIpAddr.hasMoreElements()) {
                InetAddress inetAddress = enumIpAddr.nextElement();

                if (!inetAddress.isLoopbackAddress()
                        && !inetAddress.isLinkLocalAddress()
                        && inetAddress.isSiteLocalAddress()) {
                    ip = inetAddress.getHostAddress();
                }
            }
        }
        return ip;
    }

    public static boolean isNameResolved(InetAddress address) {
        String hostname = address.getHostName();
        String ip = address.getHostAddress();
        return !hostname.equals(ip);
    }
}
