package learn.datasource.entity;

import lombok.Data;

/**
 * @author yangming
 * @date 2020/12/2
 */
@Data
public class AccountExtend {

    private Long id;

    private Long uid;

    private String exam;

    private String testType;

    private String segment;

    private String course;

    private String province;
}
