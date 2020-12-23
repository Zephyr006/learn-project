package learn.light4j.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Zephyr
 * @date 2020/12/2.
 */
@Data
@AllArgsConstructor(staticName = "of")
public class Result {

    //private Integer statusCode;

    private String code;

    private String message;

    private Object data;


}
