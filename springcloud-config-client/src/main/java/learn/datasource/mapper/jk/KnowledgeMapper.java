package learn.datasource.mapper.jk;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import learn.datasource.datasource.JkDataSource;
import learn.datasource.entity.jk.Knowledge;
import learn.datasource.registrar.DataServerMapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author caoyanan
 * @date 2021/1/14
 */
@DataServerMapper(dataSource = JkDataSource.class)
public interface KnowledgeMapper extends BaseMapper<Knowledge> {

    /**
     * 根据知识点id查询知识点
     * @param ids
     * @return
     */
    default List<Knowledge> findByIdInAndStatusTrue(Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return selectList(Wrappers.<Knowledge>lambdaQuery()
                .in(Knowledge::getId, ids)
                .eq(Knowledge::getStatus, Boolean.TRUE));
    }

    default List<Knowledge> findByStatusTrue() {
        return selectList(Wrappers.<Knowledge>lambdaQuery()
                .eq(Knowledge::getStatus, Boolean.TRUE));
    }

}
