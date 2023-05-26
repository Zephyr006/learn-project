package learn.example.javase.io.filedb.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * @author Zephyr
 * @since 2020-05-21.
 */
public class FileChannelUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileChannelUtils.class);
    //public static final CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder();
    //public static final CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();


    public static int write(FileChannel channel, byte[] context) throws IOException {
        return FileChannelUtils.write(channel, context, channel.position());
    }

    /**
     * 将内容context写入文件通道
     * @param channel FileChannel：要写入内容的通道
     * @param context   要写入的数据
     * @param startPosition 要写入内容的文件起始位置
     * @return 实际写入的字节数
     */
    public static int write(FileChannel channel, byte[] context, long startPosition) {
        if (context == null)  return 0;
        try {
            ByteBuffer buf = ByteBuffer.wrap(context);
            // 如果上一步只是申请了内存，则需要调用put将缓存放入buffer
            //buf.put(context);
            //buf.flip();      // buffer转换为读取模式,如果是调用了put方法，则必须在读取前调用
            int writeLen = channel.write(buf, startPosition);
            channel.position(channel.position() + context.length);
            return writeLen;
        } catch (IOException e) {
            logger.error("FileChannelUtils 写入数据时出现异常,写入的值为"+new String(context), e);
            return 0;
        }
    }

    public static void main(String[] args) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(
                "D:\\Dong\\code\\bayss\\bws-basic-edition\\bws-agent\\LocalStorage\\Aee.db", "rw");
        FileChannel channel = randomAccessFile.getChannel();

        FileLock lock = channel.lock(0, Short.MAX_VALUE, false);
        //if (lock.isValid())
            write(channel, new byte[]{21,21,43,34,67,7,23});
        channel.force(true);
        lock.release();
    }

    /*private static void write(FileChannel channel, byte[] context, long startPosition) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(context.length);

        buf.clear();
        buf.put(context);
        buf.flip();   // buffer转换为读取模式
        channel.write(buf, startPosition);
        //while(buf.hasRemaining()) {
        //    channel.write(buf, position);
        //}
    }*/

    /**
     * 使用指定的FileChannel，从目标位置（destPosition）开始读取指定长度（length）的字节
     * @param channel      要读取的FileChannel
     * @param destPosition 要读取的文件的起始位置
     * @param length       要读取的长度
     * @return
     * @throws IOException
     */
    public static byte[] read(FileChannel channel, long destPosition, int length) throws IOException {
        if (length < 1)  return null;
        try {
            ByteBuffer buffer = ByteBuffer.allocate(length);
            int readCount = channel.read(buffer, destPosition);
            if (readCount == -1)
                return null;
            buffer.flip();
            return buffer.array();
        } catch (IOException e) {
            logger.error("FileChannelUtil读取文件内容时出错", e);
            return null;
        }


        /*StringBuilder builder = new StringBuilder();
        CharBuffer charBuffer = CharBuffer.allocate(length);
        CoderResult result = decoder.decode(buffer, charBuffer, false);
        if (result.isError()) {
            System.err.println("CoderResult error");
        }
        charBuffer.flip();
        builder.append(charBuffer);
        charBuffer.clear();
        buffer.compact();

        return successfulRead ? builder.toString() : "";*/
        //while (buffer.hasRemaining()) {
            //byte b = buffer.get();
            //System.out.print((char)buffer.get());
        //}
        //return null;
    }

    /**
     * 方法中的“1024”必须同时修改
     * @return
     * @throws IOException
     */
    public static char[] readAll(FileChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        int readCount,curPosition = 0;
        while (true) {
            readCount = channel.read(buffer, curPosition);
            if (readCount == -1) {
                return null;
            }
            buffer.flip();
            while (buffer.hasRemaining()) {
                //byte b = buffer.get();
                System.out.print((char)buffer.get());
            }
            curPosition += readCount;
            buffer.clear();   // buffer的游标归位。不然死循环
        }
    }
}
