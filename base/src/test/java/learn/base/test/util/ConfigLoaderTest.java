package learn.base.test.util;

import learn.base.utils.ConfigLoader;
import org.junit.Test;

/**
 * @author Zephyr
 * @date 2020/12/29.
 */
public class ConfigLoaderTest {
    String path = "/app.yaml";

    @Test
    public void loadYaml() {

        Object yaml = ConfigLoader.loadYaml(path);
        System.out.println(yaml);
    }

    @Test
    public void loadText() {
        String s = ConfigLoader.loadText(path);
        System.out.println(s);
    }
}