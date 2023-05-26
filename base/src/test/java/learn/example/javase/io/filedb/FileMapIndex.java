package learn.example.javase.io.filedb;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 *
 * 索引文件数据结构： {index_start_position}:{key_hashCode}:{db_start_position}:{value_full_length}
 * @author Zephyr
 * @since 2020-05-25.
 */
public class FileMapIndex implements Serializable {
    private static final long serialVersionUID = 977629995856925165L;
    protected static final Charset CHARSET = StandardCharsets.UTF_8;

    //单条完整索引的固定长度（空间换时间） 以换行符结尾
    public static transient final int INDEX_LENGTH = 34+1;
    //分隔符
    public static transient final char SEPARATOR = ':';
    //占位符：不满 {IDX_MAX_LENGTH} 位的以 {filling} 填充
    public static transient final char FILLING = ' ';
    //完整索引字符串的结尾标识
    protected static transient final char END_CHAR = 10;
    //表示被擦除的索引
    public static transient String BLANK_IDX;
    public static transient byte[] BLANK_IDX_BYTES;
    //初始化 BLANK_IDX 和 BLANK_IDX_BYTES
    static {
        char[] chars = new char[INDEX_LENGTH];
        chars[INDEX_LENGTH -1] = END_CHAR;
        for (int i = 0; i< INDEX_LENGTH -2; i++)
            chars[i] = FILLING;
        BLANK_IDX = new String(chars);
        BLANK_IDX_BYTES = FileMapIndex.BLANK_IDX.getBytes(CHARSET);
    }


    /**  本条索引在 索引 文件中的开始位置  */
    public long indexPosition;
    /**  map 中 key 的hash值  */
    public int hash;
    /**  value 在 db 文件中存储值的开始位置  */
    public long dbStartPosition;
    /**  value 在 db 文件中存储值锁占用的长度  */
    public int valLength;

    public FileMapIndex(long indexPosition, int hash, long dbStartPosition, int valLength) {
        this.hash = hash;
        this.indexPosition = indexPosition;
        this.dbStartPosition = dbStartPosition;
        this.valLength = valLength;
    }

    /**
     * 使用长度为 IDX_MAX_LENGTH 的、满足index格式要求的字符串还原索引对象
     * @param indexStr 不能为空，且必须严格满足索引的格式
     */
    public FileMapIndex(String indexStr) {
        String[] strings = StringUtils.split(indexStr.trim(), SEPARATOR);
        this.indexPosition = Long.parseLong(strings[0]);
        this.hash = Integer.parseInt(strings[1]);
        this.dbStartPosition = Long.parseLong(strings[2]);
        this.valLength = Integer.parseInt(strings[3]);
    }

    //protected static long getHash(String indexStr) {
    //    return Long.parseLong(indexStr.substring(0, indexStr.indexOf(SEPARATOR)));
    //}

    /**
     * 获取db中存储value的实际开始位置
     * @param indexStr 一条完整索引字符串
     * @return value在db文件中的开始位置
     */
    protected static long getStartPosition(String indexStr) {
        int second = indexStr.indexOf(SEPARATOR, indexStr.indexOf(SEPARATOR)+1);
        int third = indexStr.lastIndexOf(SEPARATOR);
        return Long.parseLong(indexStr.substring(second+1, third));
    }

    /**
     * 获取db中 value 的实际长度
     * @param indexStr 一条完整索引字符串
     * @return value在db文件中的实际长度
     */
    protected static int getValLength(String indexStr) {
        indexStr = indexStr.trim();
        return Integer.parseInt(indexStr.substring(indexStr.lastIndexOf(SEPARATOR)+1));
    }

    // serialize this index
    public byte[] serialize() {
        return this.toFormatString().getBytes(CHARSET);
    }

    // deserialize specific bytes to a index
    //public static FileMapIndex deserialize(byte[] bytes) {
    //    return new FileMapIndex(new String(bytes, CHARSET));
    //}


    /**
     * 将 FileDbIndex 格式化为索引中实际存储的字符串格式
     * @return
     */
    protected String toFormatString() {
        return FileMapIndex.splice(this.indexPosition, this.hash, this.dbStartPosition, this.valLength);
    }

    /**
     * 将整型数组以 #separator 作为分隔符拼接为一个字符串
     * 并且拼接成固定长度（ IDX_MAX_LENGTH ），不够的部分以占位符 #FILLING 填充
     * @param values
     * @return
     */
    private static synchronized String splice(long... values) {
        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < values.length; buffer.append(values[i++])) {
            if (buffer.length() != 0) {
                buffer.append(SEPARATOR);
            }
        }

        //buffer.delete(buffer.length()-2, buffer.length()-1);
        if (buffer.length() > INDEX_LENGTH -1) {
            //throw new Exception("LocalDB中的索引建立失败 - 建立的索引超出最大长度限制");
        }
        for (int i = INDEX_LENGTH - buffer.length()-1; i > 0; i--) {
            buffer.append(FILLING);
        }
        buffer.append(END_CHAR);  // '\n'
        return buffer.toString();
    }

    @Override
    public String toString() {
        return "FileDbIndex{" +
                "indexPosition=" + indexPosition +
                ", startPosition=" + dbStartPosition +
                ", valLength=" + valLength +
                '}';
    }
}
