package learn.springboot.es;

import learn.springboot.es.entity.EsTestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Zephyr
 * @since 2021-11-27.
 */
@Repository
public interface TestEntityRepository extends ElasticsearchRepository<EsTestEntity, Integer> {

    Page<EsTestEntity> findByNameAndPhone(String name, String phone, Pageable pageable);
}
