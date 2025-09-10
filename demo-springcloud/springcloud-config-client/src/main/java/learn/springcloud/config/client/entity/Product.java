package learn.springcloud.config.client.entity;

import lombok.Data;

@Data
public class Product {
    private Long id;
    private String name;
    private Integer quantity;
}
