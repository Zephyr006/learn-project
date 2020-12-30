package learn.light4j.custom;

import com.networknt.config.Config;
import com.networknt.handler.Handler;
import com.networknt.handler.MiddlewareHandler;
import com.networknt.httpstring.AttachmentConstants;
import com.networknt.metrics.JVMMetricsInfluxDbReporter;
import com.networknt.metrics.MetricsConfig;
import com.networknt.server.Server;
import com.networknt.utility.Constants;
import com.networknt.utility.ModuleRegistry;
import com.networknt.utility.Util;
import io.dropwizard.metrics.Clock;
import io.dropwizard.metrics.MetricFilter;
import io.dropwizard.metrics.MetricName;
import io.dropwizard.metrics.MetricRegistry;
import io.dropwizard.metrics.influxdb.InfluxDbHttpSender;
import io.dropwizard.metrics.influxdb.InfluxDbReporter;
import io.dropwizard.metrics.influxdb.InfluxDbSender;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 自定义性能监控组件
 *
 * 1. 现在influxdb的 host 和 port 属性值可以从系统属性中设置
 * 2. 修复Light4j的bug：请求处理指标组件（MetricsHandler）在初始化端口（post）信息时，Server 还没有初始化端口信息
 *                    导致MetricsHandler获取`Server.currentPort`的值为 0。原因为初始化顺序错误
 *
 * @author Zephyr
 * @date 2020/12/7.
 */
public class CustomMetricsHandler implements MiddlewareHandler {

    public static final String CONFIG_NAME = "metrics";
    public static final MetricsConfig config;

    static final MetricRegistry registry = new MetricRegistry();

    static final Logger logger = LoggerFactory.getLogger(CustomMetricsHandler.class);

    static {
        config = (MetricsConfig)Config.getInstance().getJsonObjectConfig(CONFIG_NAME, MetricsConfig.class);
        // initialize reporter and start the report scheduler if metrics is enabled
        if(config.isEnabled() || Boolean.parseBoolean(System.getProperty("metrics.enabled", "false"))) {
            try {
                String influxdbHost = System.getProperty("influxdb.host", config.getInfluxdbHost());
                int influxdbPort = Integer.parseInt(System.getProperty("influxdb.port", String.valueOf(config.getInfluxdbPort())));
                String influxdbName = System.getProperty("influxdb.name", config.getInfluxdbName());

                InfluxDbSender influxDb =
                        new InfluxDbHttpSender(config.getInfluxdbProtocol(), influxdbHost, influxdbPort,
                                influxdbName, config.getInfluxdbUser(), config.getInfluxdbPass());
                InfluxDbReporter reporter = InfluxDbReporter
                        .forRegistry(registry)
                        .convertRatesTo(TimeUnit.SECONDS)
                        .convertDurationsTo(TimeUnit.MILLISECONDS)
                        .filter(MetricFilter.ALL)
                        .build(influxDb);
                reporter.start(config.getReportInMinutes(), TimeUnit.MINUTES);
                if (config.isEnableJVMMonitor()) {
                    createJVMMetricsReporter(influxDb);
                }

                logger.info("metrics is enabled and reporter is started");
            } catch (Exception e) {
                // if there are any exception, chances are influxdb is not available. disable this handler.
                logger.error("metrics is disabled as it cannot connect to the influxdb", e);
                // reset the enabled to false to make sure that server/info reports the right status.
                config.setEnabled(false);
            }
        }
    }

    private volatile HttpHandler next;
    static Map<String, String> commonTags = new HashMap<>();

    public CustomMetricsHandler() {
        commonTags.put("api", Server.getServerConfig().getServiceId());
        commonTags.put("env", Server.getServerConfig().getEnvironment());
        commonTags.put("addr", Server.currentAddress);
        commonTags.put("port", "" + Server.currentPort);
        InetAddress inetAddress = Util.getInetAddress();
        commonTags.put("host", inetAddress == null ? "unknown" : inetAddress.getHostName()); // will be container id if in docker.

        if(logger.isDebugEnabled()) {
            logger.debug(commonTags.toString());
        }
        if ("0".equals(commonTags.get("port"))) {
            waitForServerInit();
        }
    }

    @Override
    public HttpHandler getNext() {
        return this.next;
    }

    @Override
    public MiddlewareHandler setNext(final HttpHandler next) {
        Handlers.handlerNotNull(next);
        this.next = next;
        return this;

    }

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {
        long startTime = Clock.defaultClock().getTick();
        exchange.addExchangeCompleteListener((exchange1, nextListener) -> {
            Map<String, Object> auditInfo = exchange1.getAttachment(AttachmentConstants.AUDIT_INFO);
            if(auditInfo != null) {
                Map<String, String> tags = new HashMap<>();
                tags.put("endpoint", (String)auditInfo.get(Constants.ENDPOINT_STRING));
                tags.put("apiName", (String)auditInfo.get(Constants.ENDPOINT_STRING));
                tags.put("clientId", auditInfo.get(Constants.CLIENT_ID_STRING) != null ? (String)auditInfo.get(Constants.CLIENT_ID_STRING) : commonTags.get("host"));

                long time = Clock.defaultClock().getTick() - startTime;
                MetricName metricName = new MetricName("response_time");
                metricName = metricName.tagged(commonTags);
                metricName = metricName.tagged(tags);
                registry.getOrAdd(metricName, MetricRegistry.MetricBuilder.TIMERS).update(time, TimeUnit.NANOSECONDS);
                incCounterForStatusCode(exchange1.getStatusCode(), commonTags, tags);
            }
            nextListener.proceed();
        });

        Handler.next(exchange, next);
    }

    @Override
    public boolean isEnabled() {
        return config.isEnabled();
    }

    @Override
    public void register() {
        ModuleRegistry.registerModule(this.getClass().getName(), Config.getInstance().getJsonMapConfigNoCache(CONFIG_NAME), null);
    }

    private void incCounterForStatusCode(int statusCode, Map<String, String> commonTags, Map<String, String> tags) {
        MetricName metricName = new MetricName("request").tagged(commonTags).tagged(tags);
        registry.getOrAdd(metricName, MetricRegistry.MetricBuilder.COUNTERS).inc();
        if(statusCode >= 200 && statusCode < 400) {
            metricName = new MetricName("success").tagged(commonTags).tagged(tags);
            registry.getOrAdd(metricName, MetricRegistry.MetricBuilder.COUNTERS).inc();
        } else if(statusCode == 401 || statusCode == 403) {
            metricName = new MetricName("auth_error").tagged(commonTags).tagged(tags);
            registry.getOrAdd(metricName, MetricRegistry.MetricBuilder.COUNTERS).inc();
        } else if(statusCode >= 400 && statusCode < 500) {
            metricName = new MetricName("request_error").tagged(commonTags).tagged(tags);
            registry.getOrAdd(metricName, MetricRegistry.MetricBuilder.COUNTERS).inc();
        } else if(statusCode >= 500) {
            metricName = new MetricName("server_error").tagged(commonTags).tagged(tags);
            registry.getOrAdd(metricName, MetricRegistry.MetricBuilder.COUNTERS).inc();
        }
    }

    private static void createJVMMetricsReporter(final InfluxDbSender influxDb) {
        Map<String, String> commonTags = new HashMap<>();

        commonTags.put("api", Server.getServerConfig().getServiceId());
        commonTags.put("env", Server.getServerConfig().getEnvironment());
        commonTags.put("addr", Server.currentAddress);
        commonTags.put("port", "" + Server.currentPort);
        InetAddress inetAddress = Util.getInetAddress();
        commonTags.put("host", inetAddress == null ? "unknown" : inetAddress.getHostName()); // will be container id if in docker.

        JVMMetricsInfluxDbReporter jvmReporter = new JVMMetricsInfluxDbReporter(new MetricRegistry(), influxDb, "jvmInfluxDb-reporter",
                MetricFilter.ALL, TimeUnit.SECONDS, TimeUnit.MILLISECONDS, commonTags);
        jvmReporter.start(config.getReportInMinutes(), TimeUnit.MINUTES);
    }


    /**
     * 由于组件初始化顺序问题，当组件读取Server.currentPort的值用来初始化commonTags时，
     * Server.currentPort其实还没初始化，所以要在主线程初始化完成后再次尝试初始化commonTags
     */
    private void waitForServerInit() {
        new Thread(() -> {
            while (Server.currentPort == 0) {
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                } catch (InterruptedException ignore) { }
            }
            // init again
            if (commonTags.get("addr") == null) {
                String ipAddress = this.getIpAddress();
                commonTags.put("addr", ipAddress);
            }
            commonTags.put("port", "" + Server.currentPort);
            logger.info("MetricsHandler commonTags init again, now commonTags = " + commonTags);
        }).start();
    }

    // in kubernetes pod, the hostIP is passed in as STATUS_HOST_IP environment variable.
    // If this is null, then get the current server IP as it is not running in Kubernetes.
    private String getIpAddress() {
        String ipAddress = System.getenv("STATUS_HOST_IP");
        logger.info("Registry IP from STATUS_HOST_IP is " + ipAddress);
        if (ipAddress == null) {
            InetAddress inetAddress = null;
            try {
                inetAddress = InetAddress.getLocalHost();
            } catch (IOException ioe) {
                logger.error("Error in getting InetAddress", ioe);
            }
            assert inetAddress != null;
            ipAddress = inetAddress.getHostAddress();
            logger.info("Could not find IP from STATUS_HOST_IP, use the InetAddress " + ipAddress);
        }
        return ipAddress;
    }
}
