package learn.simple.springboot.es;

import learn.simple.springboot.es.entity.EsTestEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author Zephyr
 * @date 2021/11/27.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class EsApiTest {
    @Resource
    TestEntityRepository testEntityRepository;


    @Test
    public void testInsert() {
        final String name = "first name";
        final String phone = "17896655689";
        final EsTestEntity entity = new EsTestEntity();
        entity.setId(1).setName(name).setAge((short) 28).setPhone(phone);

        // 如果指定id的document不存在，则新建document，如果已经存在，则全量替换已有document内容
        EsTestEntity save = testEntityRepository.save(entity);
        final Optional<EsTestEntity> entityOptional = testEntityRepository.findById(entity.getId());
        Assert.assertTrue("es没有查到已经保存的数据", entityOptional.isPresent());
        entityOptional.ifPresent(System.out::println);


        final Page<EsTestEntity> entityPage = testEntityRepository.findByNameAndPhone(name, phone, PageRequest.of(0, 3));
        System.out.println(entityPage);
    }

}
