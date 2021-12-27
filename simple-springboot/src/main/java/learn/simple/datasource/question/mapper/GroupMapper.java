package learn.simple.datasource.question.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import learn.simple.datasource.question.datasource.QuestionDataSource;
import learn.simple.datasource.question.entity.Group;
import learn.simple.springboot.extradatasource.registrar.DataServerMapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@DataServerMapper(dataSource = QuestionDataSource.class)
public interface GroupMapper extends BaseMapper<Group> {

    default List<Group> findByIdInAndStatusTrue(Collection<Long> groupIdList) {
        if (CollectionUtils.isEmpty(groupIdList)) {
            return Collections.emptyList();
        }
        return selectList(Wrappers.<Group>lambdaQuery()
                .in(Group::getId, groupIdList)
                .eq(Group::getStatus, Boolean.TRUE));
    }

    default Group findByIdAndStatusTrue(Long groupId) {
        if (groupId == null) {
            return null;
        }
        return selectOne(Wrappers.<Group>lambdaQuery()
                .eq(Group::getId, groupId)
                .eq(Group::getStatus, Boolean.TRUE));
    }

}
