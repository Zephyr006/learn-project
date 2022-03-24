package learn.base.test.business;

import com.alibaba.fastjson.JSON;
import com.zaxxer.hikari.HikariDataSource;
import learn.base.test.business.entity.LessonWatchTimeLog;
import learn.base.test.business.mapper.LessonWatchTimeLogMapper;
import learn.base.utils.HikariConfigUtil;
import learn.base.utils.MybatisUtils;
import org.apache.http.HttpHost;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.util.List;
import java.util.Objects;


/**
 * @author Zephyr
 * @date 2021/12/3.
 */
public class TransDataToElasticsearchTest {

    //@Test
    public void testTransData() {
        String host = "gaea-db.rwlb.rds.aliyuncs.com:3306";
        String dbName = "lesson";
        String username = "questionbank_r";
        String pwd = "JustDoIt2019";

        final RestClientBuilder clientBuilder = RestClient.builder(
                new HttpHost("39.106.73.19", 10018, "http"));


        HikariDataSource dataSource = new HikariDataSource(HikariConfigUtil.buildHikariConfig(host, dbName, username, pwd));
        SqlSessionFactory sqlSessionFactory = MybatisUtils.getSqlSessionFactory(
                dataSource, "learn.base.test.business.mapper");


        long startId = 1203000L;
        long nowTime = System.currentTimeMillis();
        try (SqlSession sqlSession = sqlSessionFactory.openSession();
             final RestHighLevelClient restHighLevelClient = new RestHighLevelClient(clientBuilder)) {
            LessonWatchTimeLogMapper lessonWatchTimeLogMapper = sqlSession.getMapper(LessonWatchTimeLogMapper.class);

            List<LessonWatchTimeLog> lessonWatchTimeLogs;
            do
            {
                lessonWatchTimeLogs = lessonWatchTimeLogMapper.selectListByIdGt(startId, 1000);
                if (lessonWatchTimeLogs == null || lessonWatchTimeLogs.isEmpty()) {
                    break;
                }

                BulkRequest bulkRequest = new BulkRequest();
                for (LessonWatchTimeLog lessonWatchTimeLog : lessonWatchTimeLogs) {
                    bulkRequest.add(new IndexRequest("jk_lessonwatchtimelog")
                            .id(String.valueOf(lessonWatchTimeLog.getId()))
                            .source(Objects.requireNonNull(JSON.toJSONString(lessonWatchTimeLog)), XContentType.JSON));
                }
                BulkResponse response = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                if (response.status().getStatus() != 200 || response.hasFailures()) {
                    throw new IllegalStateException(response.buildFailureMessage());
                }
                startId = lessonWatchTimeLogs.stream().mapToLong(LessonWatchTimeLog::getId).max().getAsLong();
                System.out.println("当前已经同步的最大id值为: [ " + startId + " ], total cost time(s) = " + (System.currentTimeMillis() - nowTime) / 1000);
            } while (true);

        } catch (IOException e) {
            System.err.println("当前已经同步的最大id值为: " + startId);
            e.printStackTrace();
        }
    }
}
