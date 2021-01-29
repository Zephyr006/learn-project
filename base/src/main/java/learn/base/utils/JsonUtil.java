package learn.base.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 使用 fastjson 的 api 形式定义 Jackson 的工具类
 * 参照 @see https://github.com/oleg-cherednik/JacksonUtils
 *
 * @author Zephyr
 * @date 2021/1/29.
 */
public class JsonUtil {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
    private static ObjectMapper MAPPER = buildObjectMapper();

    private static ObjectMapper buildObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return mapper;
    }


    public static String toJSONString(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("JsonUtil.toJSONString error. ", e);
        }
        return null;
    }

    public static <T> T parseObject(String text, Class<T> clazz) {
        try {
            return MAPPER.readValue(text, clazz);
        } catch (JsonProcessingException e) {
            logger.error("JsonUtil.parseObject error. ", e);
        }
        return null;
    }

    public static <T> T parseObject(byte[] bytes, Class<T> clazz) {
        try {
            return MAPPER.readValue(bytes, clazz);
        } catch (IOException e) {
            logger.error("JsonUtil.parseObject error. ", e);
        }
        return null;
    }

    public static <T> List<T> parseArray(String text, Class<T> clazz) {
        try {
            ObjectReader reader = MAPPER.readerFor(clazz);
            return reader.<T>readValues(text).readAll();
        } catch (IOException e) {
            logger.error("JsonUtil.parseArray error. ", e);
        }
        return null;
    }

    public static <T> List<T> parseArrayFromFile(String filePath, Class<T> clazz) {
        try {
            String json = MAPPER.readValue(new File(filePath), String.class);
            return parseArray(json, clazz);
        } catch (IOException e) {
            logger.error("JsonUtil.parseArrayFromFile error. ", e);
        }
        return null;
    }

    public static Map<String, ?> parseMap(String text) {
        try {
            MapType mapType = MAPPER.getTypeFactory().constructRawMapType(LinkedHashMap.class);
            return MAPPER.readValue(text, mapType);
        } catch (JsonProcessingException e) {
            logger.error("JsonUtil.parseMap error. ", e);
        }
        return null;
    }


}
