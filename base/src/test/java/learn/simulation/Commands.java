package learn.simulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Zephyr
 * @date 2023-07-06
 */
public class Commands {
    public static final Pattern PATTERN_IP = Pattern.compile("(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})");

    public static void clearDnsCache() {
        // 获取操作系统版本
        String os = System.getProperty("os.name");
        if (os.contains("Windows")) {

        }
        if (os.contains("Mac")) {
            List<String> exec = exec("sudo dscacheutil -flushcache; sudo killall -HUP mDNSResponder");
            System.out.println(os + " " + exec);
        }
    }

    // nslookup GitHub.com 114.114.114.114
    public static String nslookup(String host, String dns) {
        List<String> exec = exec("nslookup " + host + " " + dns);
        if (exec == null || exec.isEmpty()) {
            throw new IllegalStateException("nslookup failed");
        } else {
            List<String> ipList = exec.stream()
                .map(line -> {
                    if (line.startsWith("Address:")) {
                        String ip = matchIp(line);
                        return dns.equals(ip) ? null : ip;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            if (ipList.size() > 1) {
                System.err.println("  nslookup对于host["+ host +"]有多个结果:" + ipList);
            }
            return ipList.isEmpty() ? "" : ipList.get(0);
        }
    }

    public static List<String> detectDnsConfiguration() {
        String os = System.getProperty("os.name");
        List<String> result = new ArrayList<>(4);
        if (os.contains("Windows")) {
            throw new UnsupportedOperationException("Windows not supported");
        }
        // cat /etc/resolv.conf
        if (os.contains("Mac")) {
            File dnsCache = new File("/etc/resolv.conf");
            if (dnsCache.exists() && dnsCache.canRead()) {
                try (Scanner scanner = new Scanner(dnsCache);) {
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        if (!line.startsWith("#") && line.contains("nameserver")) {
                            Matcher matcher = PATTERN_IP.matcher(line);
                            if (matcher.find()) {
                                result.add(matcher.group(0));
                            }
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                throw new IllegalStateException("目标DNS文件不存在或不可读,请检查目标文件");
            }
        }
        return result;
    }

    public static int ping(String host) {
        String cmd = "ping " + host + " -c 6 -W 500";
        List<String> result = exec(cmd);
        if (result == null || result.isEmpty()) {
            return -1;
        }
        Pattern pattern = Pattern.compile("round-trip min/avg/max/stddev = \\d+\\.\\d+/(\\d+\\.\\d+)/\\d+\\.\\d+/\\d+\\.\\d+");
        Matcher matcher = pattern.matcher(result.get(result.size() - 1));
        if (matcher.find()) {
            String val = matcher.group(1);
            return new BigDecimal(val).intValue();
        }
        //round-trip min/avg/max/stddev = 0.040/0.093/0.172/0.049 ms
        return -1;
    }

    public static String matchIp(String s) {
        Matcher matcher = PATTERN_IP.matcher(s);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }

    /**
     * @return null if error
     */
    public static List<String> exec(String command) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
            // 等待命令执行完成, the value 0 indicates normal termination
            int retCode = process.waitFor();

            List<String> result = new ArrayList<>();
            try (InputStream is = process.getInputStream();
                 Scanner input = new Scanner(is)) {
                while (input.hasNextLine()) {
                    result.add(input.nextLine());
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return null;
    }
}
