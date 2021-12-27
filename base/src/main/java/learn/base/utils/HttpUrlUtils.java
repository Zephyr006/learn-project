package learn.base.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * @author Zephyr
 * @date 2021/12/21.
 */
public class HttpUrlUtils {

    public static String sendGetRequest(String completeUrl) throws IOException {
        URL url = new URL(completeUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        if (HttpURLConnection.HTTP_OK != connection.getResponseCode()) {
            System.err.println(connection);
            return connection.toString();
        }

        String temp, resp = "";
        try (InputStreamReader in = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(in)) {
            while ((temp = reader.readLine()) != null) {
                resp = resp.concat(temp);
            }
        } finally {
            connection.disconnect();
        }
        return resp;
    }

    public static String sendPostRequest(String url, Map<String, String> headers, String serializedParams) throws IOException {
        URL realUrl = new URL(url);
        // 打开和URL之间的连接
        HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
        // 设置请求方法
        conn.setRequestMethod("POST");
        // 设置通用的请求属性
        Optional.ofNullable(headers).ifPresent(hs -> hs.forEach(conn::setRequestProperty));
        // 发送POST请求必须设置如下两行
        conn.setDoOutput(true);
        conn.setDoInput(true);
        // 获取URLConnection对象对应的输出流
        try (PrintWriter out = new PrintWriter(conn.getOutputStream());){
            // 发送请求参数
            out.write(serializedParams);
            // flush输出流的缓冲
            out.flush();

            // 定义BufferedReader输入流来读取URL的响应
            String result;
            try (InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
                 BufferedReader in = new BufferedReader(inputStreamReader);) {
                // 一般的返回结果数据都在同一行，所以预读取一行返回结果，可以避免字符串拼接
                result = in.readLine();
                String line;
                while ((line = in.readLine()) != null) {
                    result = result.concat(line);
                }
            }
            return result;
        } finally {
            conn.disconnect();
        }
    }

    /**
     * 序列化请求参数：只能处理String、Long这种简单类型的参数，不支持数组和对象类型的参数
     */
    public static String serializeParams(String contentType, Map<String, ?> params) throws IOException {
        switch (contentType) {
            case "application/x-www-form-urlencoded":
                return params.entrySet().stream()
                        .map(entry -> entry.getKey() + "=" + entry.getValue())
                        .collect(Collectors.joining("&"));
            case "application/json":
                StringJoiner stringJoiner = new StringJoiner(",", "{", "]");
                params.forEach((key, value) -> {
                    if (value instanceof Number) {
                        stringJoiner.add("\"" + key + "\":" + value);
                    } else {
                        stringJoiner.add("\"" + key + "\":\"" + value + "\"");
                    }
                });
                return stringJoiner.toString();
            default:
                throw new IllegalArgumentException("暂未适配的ContentType：" + contentType);
        }
    }
}
