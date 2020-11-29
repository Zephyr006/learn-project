package learn.demo.webclient.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WebClient要调用的服务器相关信息
 *
 * @author Zephyr
 * @date 2020/11/23.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerInfo {

    /**
     * 服务器url - 对应要调用服务的类锁对应的完整url，形如{@link learn.demo.webclient.api.IBlogApi}
     */
    private String url;
}
