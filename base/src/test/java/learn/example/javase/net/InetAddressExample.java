package learn.example.javase.net;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

/**
 * @author Zephyr
 * @date 2021/12/19.
 */
public class InetAddressExample {
    private static final int port = 19999;

    @Test
    public void testInetAddress() throws UnknownHostException {
        InetAddress[] addressList = InetAddress.getAllByName("github.com");
        for (InetAddress address : addressList) {
            System.out.println(address.getHostAddress());
            System.out.println(address.getHostName());
        }
    }

    // need a Socket
    private static final class Client {
        public static void main(String[] args) throws IOException {
            InetAddress localHost = InetAddress.getByName("127.0.0.1");
            Socket socket = new Socket(localHost, port);
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write("来自客户端的消息\n".getBytes());
            outputStream.write("来自客户端的消息2\n".getBytes());
            outputStream.write("来自客户端的消息3\n".getBytes());

            // 禁用此套接字的输出流。对于TCP套接字，任何先前写入的数据都将在TCP正常连接终止序列之后发送。之后此输出流将不可以再被调用
            socket.shutdownOutput();
        }
    }

    // need a ServerSocket
    private static final class Server {
        public static void main(String[] args) throws IOException {
            ServerSocket serverSocket = new ServerSocket(port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println(socket.getInetAddress());

                InputStream inputStream = socket.getInputStream();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream(128);
                byte[] buffer = new byte[512];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }

                System.out.println(outputStream.toString(StandardCharsets.UTF_8.name()));
            }
        }
    }
}
