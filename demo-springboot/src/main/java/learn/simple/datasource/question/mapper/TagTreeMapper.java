package learn.simple.datasource.question.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import learn.simple.datasource.question.datasource.QuestionDataSource;
import learn.simple.datasource.question.entity.TagTree;
import learn.simple.springboot.extradatasource.registrar.DataServerMapper;

import java.util.List;
import java.util.Objects;

@DataServerMapper(dataSource = QuestionDataSource.class)
public interface TagTreeMapper extends BaseMapper<TagTree> {


    /**
     * 查询标签树
     * @return
     */
    default TagTree findByIdStatusTrue(Long id) {
        if (Objects.isNull(id)) {
            return null;
        }
        return selectOne(Wrappers.<TagTree>lambdaQuery()
                .eq(TagTree::getId, id)
                .eq(TagTree::getStatus, Boolean.TRUE));
    }

    /**
     * 查询所有标签树
     * @return
     */
    default List<TagTree> findByStatusTrue() {
        return selectList(Wrappers.<TagTree>lambdaQuery()
                .eq(TagTree::getStatus, Boolean.TRUE));
    }



    default List<TagTree> findByNameLikeAndStatusTrue(String name) {

        LambdaQueryWrapper<TagTree> wrapper = Wrappers.<TagTree>lambdaQuery()
                .eq(TagTree::getStatus, Boolean.TRUE);
        if (StringUtils.isNotBlank(name)) {
            wrapper.like(TagTree::getName, name);
        }
        return selectList(wrapper);
    }
}
