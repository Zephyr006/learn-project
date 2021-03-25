package learn.base.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @author Zephyr
 * @date 2021/3/25.
 */
public class FileLoader {

    public static Properties loadProperties(String filePath) {
        try (final InputStream is = loadAsInputStream(filePath)) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                return props;
            } else {
                throw new FileNotFoundException("FileLoader cannot find file: " + filePath);
            }
        } catch (IOException io) {
            throw new RuntimeException("Failed to read file", io);
        }
    }

    /**
     * Convert an InputStream into a String.
     *
     * Highest performing conversion per: https://stackoverflow.com/a/35446009
     *
     * @param filePath where the file located
     * @param charset The decoding charset to use.
     * @return The string value of the input stream, null otherwise.
     * @throws IOException If there are any issues in reading from the stream.
     */
    public static String loadString(String filePath, Charset charset) {
        try (final InputStream inputStream = loadAsInputStream(filePath)) {
            if (inputStream != null) {
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
            } else {
                throw new FileNotFoundException("FileLoader cannot find file: " + filePath);
            }
        } catch (IOException io) {
            throw new RuntimeException("Failed to read file", io);
        }
    }

    /**
     * getResourceAsStream参考文档：https://www.cnblogs.com/macwhirr/p/8116583.html
     */
    public static InputStream loadAsInputStream(String filePath) throws FileNotFoundException {
        assert filePath != null;
        File file = new File(filePath);
        if (file.isFile()) {
            return new FileInputStream(file);
        }
        if (filePath.startsWith("/")) {
            return FileLoader.class.getResourceAsStream(filePath);
        } else {
            return FileLoader.class.getClassLoader().getResourceAsStream(filePath);
        }
    }
}
