package learn.light4j.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: cao
 * @time: 2020/11/13 7:03 下午
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventTrackingMessage {


    /**
     * 用户appId
     */
    private String applicationId;

    /**
     * 用户id
     */
    private String dataCenterId;

    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 埋点id(事件类型)
     */
    private String operationId;

    /**
     * AB测试分组
     */
    private String testGroup;

    /**
     * 页面id
     */
    private String pageId;

    /**
     * 链路id
     */
    private String spanId;

    /**
     * 版本
     */
    private String version;

    /**
     * 埋点数据
     */
    private String data;

    /**
     * 时间
     */
    private Long timestamp;

    /**
     * 设备版本
     */
    private String deviceVersion;

    /**
     * 渠道id
     */
    private String channelId;

    /**
     * 应用名称
     */
    private String serviceName;
}
