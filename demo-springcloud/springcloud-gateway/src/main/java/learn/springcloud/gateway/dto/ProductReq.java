package learn.springcloud.gateway.dto;

import lombok.Data;

@Data
public class ProductReq {
    private Long pid;
    private Long userId;
    private Integer amount;
}
