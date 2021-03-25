package learn.base.test;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.MainResponse;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Zephyr
 * @date 2021/3/25.
 */
public class ElasticSearchConnectTest {
    private static final String FILE_PATH = "conn-test.properties";


    @Test
    public void testConnect() throws IOException {
        final RestClientBuilder clientBuilder = RestClient.builder(
                new HttpHost("42.193.126.83", 9200, "http"));

        // need to close
        try (final RestHighLevelClient restHighLevelClient = new RestHighLevelClient(clientBuilder)) {
            final boolean connected = restHighLevelClient.ping(RequestOptions.DEFAULT);
            System.out.println("ES connected ? " + connected);

            final MainResponse info = restHighLevelClient.info(RequestOptions.DEFAULT);
            System.out.println(info.getClusterName() + "@" + info.getVersion().getNumber() + ", " + info.getTagline());

            final ClusterHealthResponse health = restHighLevelClient.cluster().health(
                    new ClusterHealthRequest(), RequestOptions.DEFAULT);
            System.out.println(health);


            System.out.println("=== ES connect successfully. ===");
        } catch (IOException e) {
            System.err.println("=== ES connect fail. ===");
            throw e;
        }
    }

}
