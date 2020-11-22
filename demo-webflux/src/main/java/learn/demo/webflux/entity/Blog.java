package learn.demo.webflux.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * @author Zephyr
 * @date 2020/11/13.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "blog")
public class Blog {

    @Id
    private String id;

    /**
     * 博客名称
     */
    @NotBlank
    private String name;

    /**
     * 阅读量
     */
    @Min(1)
    private Integer readCount;
}
