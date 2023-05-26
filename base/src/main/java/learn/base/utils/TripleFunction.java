package learn.base.utils;

/**
 * @author Zephyr
 * @since 2022-8-26.
 */
@FunctionalInterface
public interface TripleFunction<A,B,C, R> {

    R apply(A a, B b, C c);

}
