package learn.springboot.datasource.question.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import learn.springboot.datasource.question.datasource.QuestionDataSource;
import learn.springboot.datasource.question.entity.Tag;
import learn.springboot.extradatasource.registrar.DataServerMapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@DataServerMapper(dataSource = QuestionDataSource.class)
public interface TagMapper extends BaseMapper<Tag> {


    /**
     * 查询某个标签树的所有标签
     * @return
     */
    default List<Tag> findByTagTreeIdAndStatusTrue(Long tagTreeId) {
        if (Objects.isNull(tagTreeId)) {
            return Collections.emptyList();
        }
        return selectList(Wrappers.<Tag>lambdaQuery()
                .eq(Tag::getTreeId, tagTreeId)
                .eq(Tag::getStatus, Boolean.TRUE));
    }

    default List<Tag> findByStatusTrue() {
        return selectList(Wrappers.<Tag>lambdaQuery()
                .eq(Tag::getStatus, Boolean.TRUE));
    }

    default Tag findByIdAndStatusTrue(Long id) {

        if (Objects.isNull(id)) {
            return null;
        }
        return selectOne(Wrappers.<Tag>lambdaQuery()
                .eq(Tag::getId, id)
                .eq(Tag::getStatus, Boolean.TRUE));
    }


    default List<Tag> findByTagTreeIdInAndStatusTrue(Collection<Long> tagTreeIds) {
        if (CollectionUtils.isEmpty(tagTreeIds)) {
            return Collections.emptyList();
        }
        return selectList(Wrappers.<Tag>lambdaQuery()
                .in(Tag::getTreeId, tagTreeIds)
                .eq(Tag::getStatus, Boolean.TRUE));
    }
}
