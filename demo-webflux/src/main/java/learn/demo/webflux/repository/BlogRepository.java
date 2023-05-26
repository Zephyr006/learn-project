package learn.demo.webflux.repository;

import learn.demo.webflux.entity.Blog;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * JPA可以根据方法名自动生成sql
 *
 * @author Zephyr
 * @since 2020-11-13.
 */
@Repository
public interface BlogRepository extends ReactiveMongoRepository<Blog, String> {

    Flux<Blog> findByNameLike(String name);

    @Query("{ 'readCount' : {'$gte':20, '$lte':300} }")
    Flux<Blog> findReadCountByQuery();
}
