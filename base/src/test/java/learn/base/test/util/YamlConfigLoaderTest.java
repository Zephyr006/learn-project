package learn.base.test.util;

import learn.base.BaseTest;
import learn.base.utils.YamlConfigLoader;
import org.junit.Test;

/**
 * @author Zephyr
 * @date 2020/12/29.
 */
public class YamlConfigLoaderTest extends BaseTest {
    String path = "/app.yaml";

    @Test
    public void loadYaml() {
        if (!checkContext()) {
            return;
        }

        Object yaml = YamlConfigLoader.loadYaml(path);
        System.out.println(yaml);
    }

    @Test
    public void loadText() {
        if (!checkContext()) {
            return;
        }
        String s = YamlConfigLoader.loadText(path);
        System.out.println(s);
    }
}