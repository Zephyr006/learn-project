package learn.base.test.connect;

import learn.base.BaseTest;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.MainResponse;

import java.io.IOException;

/**
 * @author Zephyr
 * @since 2021-3-25.
 */
public class ElasticsearchConnectTest extends BaseTest {
    private static final String FILE_PATH = "conn-test.properties";


    public static void main(String[] args) throws IOException {
        new ElasticsearchConnectTest().testApi();
    }

    private RestHighLevelClient getRestHighLevelClient() {
        final RestClientBuilder clientBuilder = RestClient.builder(
                new HttpHost("192.168.2.236", 9200, "http"));
        return new RestHighLevelClient(clientBuilder);
    }

    public void testConnect() throws IOException {
        if (!checkContext()) {
            return;
        }

        // need to close
        try (final RestHighLevelClient restHighLevelClient = getRestHighLevelClient()) {
            final boolean connected = restHighLevelClient.ping(RequestOptions.DEFAULT);
            System.out.println("ES connected ? " + connected);
            System.out.println("=== ES connect successfully. ===");

            final MainResponse info = restHighLevelClient.info(RequestOptions.DEFAULT);
            System.out.println(info.getClusterName() + "@" + info.getVersion().getNumber() + ", " + info.getTagline());

            final ClusterHealthResponse health = restHighLevelClient.cluster().health(
                    new ClusterHealthRequest(), RequestOptions.DEFAULT);
            System.out.println(health);


        } catch (IOException e) {
            System.err.println("=== ES connect fail. ===");
            throw e;
        }
    }

    public void testApi() {
        try (RestHighLevelClient highLevelClient = getRestHighLevelClient()) {
            final IndexResponse indexResponse = highLevelClient.index(new IndexRequest("kibana_sample_data_ecommerce"), RequestOptions.DEFAULT);
            System.out.println(indexResponse.getIndex());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

 }
