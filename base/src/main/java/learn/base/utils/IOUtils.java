package learn.base.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author Zephyr
 * @since 2022-01-12.
 */
public class IOUtils {
    public static final char DIR_SEPARATOR_UNIX = '/';
    public static final char DIR_SEPARATOR_WINDOWS = '\\';

    public static String toString(InputStream inputStream, Charset charset) {
        charset = charset == null ? StandardCharsets.UTF_8 : charset;

        //InputStreamReader是从字节流到字符流的桥梁：它读取字节并使用指定的字符集将其解码为字符
        try (Reader reader = new InputStreamReader(inputStream, charset)) {
            int num;
            char[] buf = new char[1024];
            StringBuilder builder = new StringBuilder();
            while ((num = reader.read(buf)) != -1) {
                builder.append(buf, 0, num);
            }
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }

    public static byte[] toByteArray(InputStream inputStream) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            return bos.toByteArray();
        } catch (IOException ignore) {
            return new byte[0];
        }
    }


}
