package learn.light4j.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户 id 和 dataCenterId
 *
 * @author Zephyr
 * @since 2020-12-01.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserIdModel {

    private Long appUserId;

    private Long dataCenterId;

}
