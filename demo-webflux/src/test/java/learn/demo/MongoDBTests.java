package learn.demo;

import learn.demo.webflux.WebfluxApplication;
import learn.demo.webflux.entity.Blog;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author Zephyr
 * @since 2021-9-24.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebfluxApplication.class)
public class MongoDBTests {
    @Autowired
    private MongoTemplate mongoTemplate;


    @Test
    public void testMongoApi() {
        Class<Blog> entityClass = Blog.class;
        List<Blog> blogs = mongoTemplate.find(new Query(), entityClass);

    }

    public void testReceiveNewQuestionnaire() {

    }
}
