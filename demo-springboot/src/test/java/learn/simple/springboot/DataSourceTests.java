package learn.simple.springboot;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Zephyr
 * @since 2021-12-26.
 */
@SpringBootTest(classes = App.class)
@RunWith(SpringRunner.class)
public class DataSourceTests {
    //@Resource
    //GroupMapper groupMapper;
    //@Resource
    //LessonMapper lessonMapper;

    @Test
    public void testQuery() {
        //System.out.println(groupMapper);
        //System.out.println(groupMapper.findByIdInAndStatusTrue(Arrays.asList(7L, 8L)));
        //System.out.println(lessonMapper);
        //System.out.println(lessonMapper.findByIdInAndStatusTrue(Arrays.asList(17L, 11L)));
    }
}
