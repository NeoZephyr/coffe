package jubi.common;

import com.google.common.net.InetAddresses;
import jubi.JubiException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

@Slf4j
public class Utils {

    public static int DEFAULT_SHUTDOWN_PRIORITY = 100;
    public static int SERVER_SHUTDOWN_PRIORITY = 75;

    public static void addShutdownHook(Runnable hook, int priority) {
        ShutdownHookManager.get().addShutdownHook(hook, priority);
    }

    public static void deleteDirectoryRecursively(File file) {
        if (file.isDirectory()) {
            File[] subFiles = file.listFiles();

            for (File subFile : subFiles) {
                deleteDirectoryRecursively(subFile);
            }
        }
        file.delete();
    }

    public static String getHostIp() throws Exception {
        // For K8S, there are too many IPs, it's hard to decide which we should use.
        // So we use the environment variable to tell to use which one.
        String ip = System.getenv("JUBI_IP");
        if (ip != null) {
            if (!InetAddresses.isInetAddress(ip)) {
                throw new JubiException("Environment JUBI_IP: " + ip + " is wrong format");
            }
            return ip;
        }
        Enumeration<NetworkInterface> nif = NetworkInterface.getNetworkInterfaces();
        String siteLocalAddress = null;
        while (nif.hasMoreElements()) {
            NetworkInterface ni = nif.nextElement();
            if (!ni.isUp() || ni.isLoopback() || ni.isPointToPoint() || ni.isVirtual()) {
                continue;
            }
            for (InterfaceAddress ifa : ni.getInterfaceAddresses()) {
                InetAddress ia = ifa.getAddress();
                InetAddress brd = ifa.getBroadcast();
                if (brd == null || brd.isAnyLocalAddress()) {
                    log.info("ip {} was filtered, because it don't have effective broadcast address", ia.getHostAddress());
                    continue;
                }
                if (!ia.isLinkLocalAddress() && !ia.isAnyLocalAddress() && !ia.isLoopbackAddress()
                        && ia instanceof Inet4Address && ia.isReachable(5000)) {
                    if (!ia.isSiteLocalAddress()) {
                        return ia.getHostAddress();
                    } else if (siteLocalAddress == null) {
                        log.info("ip {} was candidate, if there is no better choice, we will choose it", ia.getHostAddress());
                        siteLocalAddress = ia.getHostAddress();
                    } else {
                        log.info("ip {} was filtered, because it's not first effect site local address", ia.getHostAddress());
                    }
                } else if (!(ia instanceof Inet4Address)) {
                    log.info("ip {} was filtered, because it's just a ipv6 address", ia.getHostAddress());
                } else if (ia.isLinkLocalAddress()) {
                    log.info("ip {} was filtered, because it's just a link local address", ia.getHostAddress());
                } else if (ia.isAnyLocalAddress()) {
                    log.info("ip {} was filtered, because it's just a any local address", ia.getHostAddress());
                } else if (ia.isLoopbackAddress()) {
                    log.info("ip {} was filtered, because it's just a loop back address", ia.getHostAddress());
                } else {
                    log.info("ip {} was filtered, because it's just not reachable address", ia.getHostAddress());
                }
            }
        }
        return siteLocalAddress;
    }
}
