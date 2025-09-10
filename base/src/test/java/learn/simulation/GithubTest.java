package learn.simulation;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

/**
 * @author Zephyr
 * @date 2023-07-06
 */
public class GithubTest {

    public static void main(String[] args) throws IOException {
        Path hosts = new File("/Users/dong/Desktop/hosts").toPath();

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

    @Data
    @AllArgsConstructor
    static class Result implements Comparable<Result> {
        private String dns;
        private String host;
        private List<String> ipList;
        private int cost;
        private int ping;
        @Override
        public int compareTo(Result other) {
            if (this.ipList == null || this.ipList.isEmpty()) {
                return 1;
            } else  if (other.ipList == null || other.ipList.isEmpty()) {
                return -1;
            } else if (this.ipList.size() != other.ipList.size()){
                return other.ipList.size() - this.ipList.size();
            // } else if (Math.abs(this.ping - other.ping) > 20) { // ip size is equal
            //     return this.ping - other.ping;
            } else {
                return this.cost - other.cost;
            }
        }
        @Override
        public String toString() {
            return String.format("DNS [%s] got %d ip of host %s, cost %d, ping %d", dns, ipList.size(), host, cost, ping);
        }
    }

    @Test
    public void testLookup() {
        List<String> dnsList = Arrays.asList(
            "223.5.5.5",     //阿里dns
            "180.76.76.76",  //百度
            "119.29.29.29",  //腾讯!
            "112.124.47.27",  //OneDns,官方说是可以屏蔽广告，加速Google等网站的访问等
            "208.67.222.222",  //OpenDns,非常知名的，但延迟有点高，在180ms左右，偶尔掉包，除了延迟大点
            "199.91.73.222",  //V2EX DNS：延迟有点高，偶尔掉包
            "114.114.114.114",  //114!
            "1.1.1.1",          //Cloudflare DNS!
            "8.8.8.8"           //Google Public DNS
        );
        TreeMap<String, Integer> dnsMap = new TreeMap<>(Comparator.reverseOrder());

        String bestDNS = tryDNS(dnsList, "github.com", dnsMap);
        bestDNS = tryDNS(dnsList, "github.githubassets.com", dnsMap);
        // bestDNS = tryDNS(dnsList, "raw.githubusercontent.com", dnsMap);
        // bestDNS = tryDNS(dnsList, "www.v2ex.com", dnsMap);
        // bestDNS = tryDNS(dnsList, "greasyfork.org", dnsMap);
        // bestDNS = tryDNS(dnsList, "google.com/chrome", dnsMap);

        System.out.printf("\n\nBest DNS: %s\n", dnsMap.firstEntry());

        for (String dns : dnsList) {
            System.out.printf("DNS %s ping %d\n", dns, CommandUtil.ping(dns));
        }
    }

    private static String tryDNS(Collection<String> dnsList, String host, TreeMap<String, Integer> dnsMap) {
        List<Result> results = new ArrayList<>();
        for (String dns : dnsList) {
            results.add(lookupIp(dns, host));
        }
        results.sort(Result::compareTo);
        System.err.printf("%s %s best DNS is [%s]\n", results.get(0).ipList.get(0), host, results.get(0).dns);
        // results.subList(0, 3).forEach(System.out::println);
        System.out.println();

        String bestDNS = results.get(0).getIpList().isEmpty() ? null : results.get(0).getDns();
        if (bestDNS != null) {
            dnsMap.compute(bestDNS, (k, v) -> v == null ? 1 : v + 1);
        }
        return bestDNS;
    }

    private static Result lookupIp(String dns, String host) {
        long nowTime = System.currentTimeMillis();
        List<String> lookupResult = CommandUtil.nslookup(host, dns);
        String firstIp = lookupResult.isEmpty() ? "" : lookupResult.get(0);
        if (firstIp.isEmpty()) {
            // System.err.printf("DNS [%s] cannot find %s's ip, not recommended! %n", dns, host);
            return new Result(dns, host, lookupResult, Integer.MAX_VALUE, Integer.MAX_VALUE);
        } else {
            int ping = CommandUtil.ping(firstIp);
            long cost = System.currentTimeMillis() - nowTime;
            // System.out.printf("DNS [%s]  find %s's ip  %s , cost ms %d , ping = %d %n",
            //     dns, host, lookupResult, cost, ping);
            return new Result(dns, host, lookupResult, (int) cost, ping);
        }
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
