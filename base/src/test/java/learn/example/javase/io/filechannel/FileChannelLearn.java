package learn.example.javase.io.filechannel;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;

/**
 * @author Zephyr
 * @since 2020-7-2.
 */
public class FileChannelLearn {

    public void learnApi() throws IOException {
        // 打开 FileChannel:通过使用一个InputStream、OutputStream或RandomAccessFile来获取一个FileChannel实例
        RandomAccessFile file = new RandomAccessFile("", "rw");
        FileChannel fileChannel = file.getChannel();

        //向FileChannel写入数据：必须通过ByteBuffer写入（单个ByteBuffer 或 ByteBuffer数组）
        byte[] bytes = "some words".getBytes(StandardCharsets.UTF_8);
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        int writeLength = fileChannel.write(byteBuffer, 0);
        fileChannel.position(fileChannel.position() + bytes.length);

        // 从FileChannel读取数据
        int read_byte_length = 100;
        long destPosition = 0;     //从文件的指定位置开始读取
        ByteBuffer buffer = ByteBuffer.allocate(read_byte_length);
        int readCount = fileChannel.read(buffer, destPosition);
        if (readCount == -1)
            System.out.println("read nothing");
        buffer.flip();
        System.out.println("read result(bytes) = " + buffer.array());

        // 获取FileChannel锁
        //获取锁定范围为 0-Long.MAX_VALUE 的独占锁
        FileLock exclusiveLock = fileChannel.lock();
        //获取自定义锁定范围的锁（可以为共享锁或独占锁），获取公平锁时，如果操作系统不支持，则返回独占锁
        FileLock sharedLock = fileChannel.lock(0, 100, true);


    }
}
