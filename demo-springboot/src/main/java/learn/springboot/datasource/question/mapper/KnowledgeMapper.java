package learn.springboot.datasource.question.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import learn.springboot.datasource.question.datasource.QuestionDataSource;
import learn.springboot.datasource.question.entity.Knowledge;
import learn.springboot.extradatasource.registrar.DataServerMapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@DataServerMapper(dataSource = QuestionDataSource.class)
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



    /**
     * 查询某一学科下所有知识点
     */
    default List<Knowledge> findByTreeIdAndStatusTrue(Long treeId) {

        if (Objects.isNull(treeId)) {
            return Collections.emptyList();
        }

        return selectList(Wrappers.<Knowledge>lambdaQuery()
                .eq(Knowledge::getRootId, treeId)
                .eq(Knowledge::getStatus, Boolean.TRUE));
    }

    /**
     * 查询多个学科下所有知识点
     */
    default List<Knowledge> findByTreeIdInAndStatusTrue(Collection<Long> treeIds) {

        if (CollectionUtils.isEmpty(treeIds)) {
            return Collections.emptyList();
        }

        return selectList(Wrappers.<Knowledge>lambdaQuery()
                .in(Knowledge::getRootId, treeIds)
                .eq(Knowledge::getStatus, Boolean.TRUE));
    }

    /**
     * 查询某个知识点的子知识点
     * @param pid
     * @return
     */
    default List<Knowledge> findByPidAndStatusTrue(Long pid) {

        if (Objects.isNull(pid)) {
            return Collections.emptyList();
        }

        return selectList(Wrappers.<Knowledge>lambdaQuery()
                .eq(Knowledge::getParentId, pid)
                .eq(Knowledge::getStatus, Boolean.TRUE));
    }

}
