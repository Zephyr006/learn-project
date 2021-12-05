package learn.simple.springboot.es.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.elasticsearch.index.VersionType;
import org.springframework.data.elasticsearch.annotations.Document;


/**
 * @author Zephyr
 * @date 2021/11/27.
 */
@Data
@Accessors(chain = true)
@Document(indexName = "test_index", shards = 1, replicas = 1,
        refreshInterval = "1s", indexStoreType = "fs", versionType = VersionType.EXTERNAL)
public class EsTestEntity {

    private Integer id;

    private String name;

    private Short age;

    private String phone;


}
