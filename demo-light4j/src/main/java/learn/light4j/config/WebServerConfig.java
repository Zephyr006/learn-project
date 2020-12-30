package learn.light4j.config;

import lombok.Data;

/**
 * @author: cao
 * @time: 2020/12/3 2:22 下午
 */
@Data
public class WebServerConfig {

    private String base;
    private Integer transferMinSize;
    private String rewrite;
}
