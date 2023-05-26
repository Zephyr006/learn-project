package learn.light4j.exception;

/**
 * @author Zephyr
 * @since 2020-12-01.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException() {
        super("JWT授权校验未通过！");
    }
}
