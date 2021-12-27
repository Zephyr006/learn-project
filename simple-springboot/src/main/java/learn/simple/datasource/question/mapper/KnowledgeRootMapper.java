package learn.simple.datasource.question.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import learn.simple.datasource.question.datasource.QuestionDataSource;
import learn.simple.datasource.question.entity.KnowledgeRoot;
import learn.simple.springboot.extradatasource.registrar.DataServerMapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@DataServerMapper(dataSource = QuestionDataSource.class)
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

    /**
     * 查询全部学科
     * @return
     */
    default List<KnowledgeRoot> findByStatusTrue() {

        return selectList(Wrappers.<KnowledgeRoot>lambdaQuery()
                .eq(KnowledgeRoot::getStatus, Boolean.TRUE));
    }


    default List<KnowledgeRoot> findByNameLikeAndStatusTrue(String name) {

        LambdaQueryWrapper<KnowledgeRoot> wrapper = Wrappers.<KnowledgeRoot>lambdaQuery()
                .eq(KnowledgeRoot::getStatus, Boolean.TRUE);
        if (StringUtils.isNotBlank(name)) {
            wrapper.like(KnowledgeRoot::getName, name);
        }
        return selectList(wrapper);
    }
}
