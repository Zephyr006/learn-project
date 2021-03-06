package learn.datasource.mapper.jk;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import learn.datasource.datasource.JkDataSource;
import learn.datasource.entity.jk.KnowledgeRoot;
import learn.datasource.registrar.DataServerMapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author caoyanan
 * @date 2021/1/14
 */
@DataServerMapper(dataSource = JkDataSource.class)
public interface KnowledgeRootMapper extends BaseMapper<KnowledgeRoot> {

    /**
     * 根据学科id查询学科
     * @param ids
     * @return
     */
    default List<KnowledgeRoot> findByIdInAndStatusTrue(Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return selectList(Wrappers.<KnowledgeRoot>lambdaQuery()
                .in(KnowledgeRoot::getId, ids)
                .eq(KnowledgeRoot::getStatus, Boolean.TRUE));
    }
}
