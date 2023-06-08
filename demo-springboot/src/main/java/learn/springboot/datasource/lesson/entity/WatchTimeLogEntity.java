package learn.springboot.datasource.lesson.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("lesson_watch_time_log")
@Data
public class WatchTimeLogEntity {

    private Long id;
    private Long lessonId;
    private Long userId;
    private Long startAt;
    private Long endAt;

    /**
     * 场景id
     */
    private Long scenesId;

    /**
     * 场景key
     */
    private Long scenesKey;

    private Long createdAt;

    private Boolean status;
}
