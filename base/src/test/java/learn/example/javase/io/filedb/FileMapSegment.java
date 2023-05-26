package learn.example.javase.io.filedb;

import java.io.Serializable;

/**
 * @author Zephyr
 * @since 2020-06-11.
 */
public class FileMapSegment<V> implements Serializable {
    private static final long serialVersionUID = 8045152871910439442L;
    private String key;
    private Long time;
    private V value;

    public FileMapSegment(String key, V value) {
        this(System.currentTimeMillis(), key, value);
    }

    public FileMapSegment(Long time, String key, V value) {
        this.key = key;
        this.value = value;
        this.time = time;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    //public byte[] serialize(ObjectMapper mapper) throws JsonProcessingException {
    //    String jsonString = this.toJsonString(mapper.writeValueAsString(value));
    //    return mapper.writeValueAsBytes(jsonString);
    //}

    public String toJsonString(String valueStr) {
        return  "{\"time\":" + time +
                ",\"key\":\"" + key +
                "\",\"value\":" + valueStr +
                "}";
    }
}
