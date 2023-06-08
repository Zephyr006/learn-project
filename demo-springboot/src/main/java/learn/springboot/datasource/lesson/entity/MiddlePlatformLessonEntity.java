package learn.springboot.datasource.lesson.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("lesson")
@Data
public class MiddlePlatformLessonEntity {

    private Long id;

    /**
     * 课件名称
     */
    private String name;

    /**
     * 作业类型0直播，1回放，2录播
     */
    private Integer lessonType;

    /**
     * cc的roomId
     */
    private String roomId;

    /**
     * 直播id唯一标识
     */
    private String liveStreamVideoId;

    /**
     * 回放id惟一标识
     */
    private String recordedVideoId;

    /**
     * 课件设置开始时间
     */
    private Long startTime;

    /**
     * 课件设置结束时间
     */
    private Long endTime;

    /**
     * 第三方服务商：1：展示，2：cc
     */
    private Integer driver;

    /**
     * 状态, 0正常，1删除
     */
    private Integer status;
}
