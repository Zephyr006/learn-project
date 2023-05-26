package learn.base.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 使用 fastjson 的 api 形式定义 Jackson 的工具类
 * 参照 @see https://github.com/oleg-cherednik/JacksonUtils
 *
 * @author Zephyr
 * @since 2021-1-29.
 */
public class JsonUtils {
    // private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
    private static ObjectMapper MAPPER = new ObjectMapper();

    static {
        // JDK8新时间类支持： 序列化时带有T的问题，自定义格式化字符串
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        MAPPER.registerModule(javaTimeModule);
        // Date (and date/time) and Date-based things like {@link java.util.Calendar}s are to be serialized as numeric timestamps
        MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        // 如果JSON字符串中包含Java类中没有的字段，不再抛出异常 (默认导致UnrecognizedPropertyException异常)
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    public static String toJSONString(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            // logger.error("JsonUtil.toJSONString error. ", e);
        }
        return null;
    }

    public static <T> T parseObject(String text, Class<T> clazz) {
        try {
            return MAPPER.readValue(text, clazz);
        } catch (JsonProcessingException e) {
            // logger.error("JsonUtil.parseObject error, original text = " + text, e);
        }
        return null;
    }

    public static <T> T parseObject(byte[] bytes, Class<T> clazz) {
        try {
            return MAPPER.readValue(bytes, clazz);
        } catch (IOException e) {
            // logger.error("JsonUtil.parseObject error. ", e);
        }
        return null;
    }

    public static <T> List<T> parseArray(String text, Class<T> clazz) {
        try {
            //return MAPPER.readValue(text, new TypeReference<List<T>>() {});
            //ObjectReader reader = MAPPER.readerFor(clazz);
            return MAPPER.readerFor(clazz).<T>readValues(text).readAll();
        } catch (IOException e) {
            // logger.error("JsonUtil.parseArray error, original text = " + text, e);
        }
        return null;
    }

    public static <T> List<T> parseArrayFromFile(String filePath) {
        try {
            return MAPPER.readValue(new File(filePath), new TypeReference<List<T>>() {});
        } catch (IOException e) {
            // logger.error("JsonUtil.parseArrayFromFile error. ", e);
        }
        return null;
    }

    public static Map<String, ?> parseMap(String text) {
        try {
            MapType mapType = MAPPER.getTypeFactory().constructRawMapType(LinkedHashMap.class);
            return MAPPER.readValue(text, mapType);
        } catch (JsonProcessingException e) {
            // logger.error("JsonUtil.parseMap error. ", e);
        }
        return null;
    }


}
