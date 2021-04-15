package learn.base.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author Zephyr
 * @date 2020/12/29.
 */
public class YamlConfigLoader {

    /**
     * 解析 yaml 配置文件
     * yaml配置文件中的 ":" 会被解析为 Map 中的key
     * 使用#load(), #loadAs()方法无法解析 yaml 文件中的"---"分隔符，必须使用 #loadAll() 方法
     */
    public static Object loadYaml(String filePath) {
        InputStream inputStream = YamlConfigLoader.loadAsInputStream(filePath);

        Yaml yaml = new Yaml();
        Object load = yaml.load(inputStream);
        // 读取为指定类型（具体类型取决于配置配置文件中的内容）
        //Map map = yaml.loadAs(inputStream, Map.class);
        return load;
    }

    public static String loadText(String filePath) {
        InputStream inputStream = YamlConfigLoader.loadAsInputStream(filePath);

        try {
            return YamlConfigLoader.inputStreamToString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * getResourceAsStream参考文档：https://www.cnblogs.com/macwhirr/p/8116583.html
     */
    private static InputStream loadAsInputStream(String filePath) {
        assert filePath != null;
        if (filePath.startsWith("/")) {
            return YamlConfigLoader.class.getResourceAsStream(filePath);
        } else {
            return YamlConfigLoader.class.getClassLoader().getResourceAsStream(filePath);
        }
    }

    /**
     * Convert an InputStream into a String.
     *
     * Highest performing conversion per: https://stackoverflow.com/a/35446009
     *
     * @param inputStream The input stream to be converted.
     * @param charset The decoding charset to use.
     * @return The string value of the input stream, null otherwise.
     * @throws IOException If there are any issues in reading from the stream.
     */
    private static String inputStreamToString(InputStream inputStream, Charset charset) throws IOException {
        if (inputStream != null && inputStream.available() != -1) {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            if (charset != null) {
                return result.toString(charset.name());
            }
            return result.toString(StandardCharsets.UTF_8.name());
        }
        return null;
    }

}
