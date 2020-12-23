package learn.light4j.exception;

/**
 * @author Zephyr
 * @date 2020/12/1.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException() {
        super("JWT授权校验未通过！");
    }
}
