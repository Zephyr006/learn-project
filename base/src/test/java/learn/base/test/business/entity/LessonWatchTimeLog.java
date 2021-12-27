package learn.base.test.business.entity;

import lombok.Data;

/**
 * 日志（用户每次看课）
 * @author : yangming
 * @date : 2019/9/23
 */
@Data
public class LessonWatchTimeLog {

    private Long id;

    /**
     * 场景Id
     */
    private Long scenesId;

    /**
     * 场景Key
     */
    private Long scenesKey;

    /**
     * 课件Id
     */
    private Long lessonId;

    /**
     * 元信息
     */
    private String metadata;

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 看课的开始时间
     */
    private Long startAt;

    /**
     * 看课的结束时间
     */
    private Long endAt;

    /**
     * 看课时课程状态,1:直播,2:录播
     */
    private Integer lessonStatus;

    /**
     * 是否在线观看
     */
    private Boolean isOnline;

    private Long createdAt;

    private Long updatedAt;

    private Boolean status;
}
