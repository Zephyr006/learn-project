package learn.simulation;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Zephyr
 * @date 2023-07-06
 */
public class GithubTest {

    public static void main(String[] args) throws IOException {
        Path hosts = new File("/Users/wangshidong/Desktop/hosts").toPath();

        if (hosts.toFile().canWrite()) {
            System.out.println("can write");
            try (BufferedReader reader = Files.newBufferedReader(hosts);
                BufferedWriter writer = Files.newBufferedWriter(hosts)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // if (line.contains("google.com")) {
                    //     writer.write("142.251.42.238 google.com");
                    // } else {
                        writer.write(line);
                    // }
                }
                writer.flush();
            }
        }

        BufferedReader reader = Files.newBufferedReader(hosts);
        reader.lines().forEach(System.out::println);
    }

    @Test
    public void testLookup() {
        List<String> dnsList = Arrays.asList(
            // "223.5.5.5",     //阿里dns
            // "180.76.76.76",  //百度
            "119.29.29.29",  //腾讯!
            "112.124.47.27",  //OneDns,官方说是可以屏蔽广告，加速Google等网站的访问等
            // "208.67.222.222",  //OpenDns,非常知名的，但延迟有点高，在180ms左右，偶尔掉包，除了延迟大点
            // "199.91.73.222",  //V2EX DNS：延迟有点高，偶尔掉包
            "114.114.114.114"  //114!
        );
        for (String dns : dnsList) {
            lookupIp(dns, "github.com");
        }
        System.out.println("");
        for (String dns : dnsList) {
            lookupIp(dns, "raw.githubusercontent.com");
        }
        for (String dns : dnsList) {
            lookupIp(dns, "google.com/chrome");
        }
        System.out.println("");
        // for (String dns : dnsList) {
        //     lookupIp(dns, "google.com");
        // }
    }

    private static String lookupIp(String dns, String host) {
        long nowTime = System.currentTimeMillis();
        String lookupResult = CommandUtil.nslookup(host, dns);
        System.out.printf("DNS [%s]  find %s's ip  %s , cost ms %d , ping = %d %n",
            dns, host, lookupResult, (System.currentTimeMillis() - nowTime), CommandUtil.ping(lookupResult));
        return lookupResult;
    }

    @Test
    public void testIpPattern() {
        String ip = CommandUtil.matchIp("Address: 20.205.243.166#1");
        assert Objects.equals(ip, "20.205.243.166");
    }

    @Test
    public void testDetectDns() {
        List<String> ipList = CommandUtil.detectDnsConfiguration();
        String dns = String.join(", ", ipList);
        System.out.println("Now dns is [" + dns + "]");
    }

    @Test
    public void testPing() {
        System.out.println("github ping = " + CommandUtil.ping("github.com") + " ms, google ping = " + CommandUtil.ping("google.com"));
    }

    @Test
    public void testClearDns() {
        CommandUtil.clearDnsCache();
    }

}
