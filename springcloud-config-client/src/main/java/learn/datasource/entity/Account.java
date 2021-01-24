package learn.datasource.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author yangming
 * @date 2020/12/2
 */
@Data
public class Account {

    private Long id;

    private String username;

    private String phone;

    private Integer status;

    private Long dataCenterId;

    private Date createTime;

    private Long latestRequestTime;
}
