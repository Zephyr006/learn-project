package learn.demo.webflux.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Zephyr
 * @date 2020/11/13.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
//@Document(collection = "blog")
public class Blog {

    //@Id
    private String id;

    private String name;
}
