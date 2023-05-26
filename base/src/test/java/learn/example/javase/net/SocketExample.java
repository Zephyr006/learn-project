package learn.example.javase.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * @author Zephyr
 * @since 2022-1-12.
 */
public class SocketExample {

    static int port = 9988;

    private static final class Client {
        public static void main(String[] args) throws IOException {
            for (int i = 0; i < 10; i++) {
                createSocketAndSendData();
            }
        }

        private static void createSocketAndSendData() throws IOException {
            Socket socket = new Socket(InetAddress.getLocalHost(), port);
            System.out.println(socket.getInetAddress());

            // 只有前面正确的建立了socket连接，才能正常运行到这里，否则直接报错"ConnectException: Connection refused"
            try (OutputStream outputStream = socket.getOutputStream()) {
                outputStream.write("这是一条来自Socket客户端的消息\n".getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                // 禁用此套接字的输出流。对于TCP套接字，任何先前写入的数据都将在TCP正常连接终止序列之后发送。
                // 如果在调用套接字上的shutdownOutput（）后写入套接字输出流，该流将抛出IOException
                socket.shutdownOutput();

                InputStream inputStream = socket.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                int r;
                StringBuilder builder = new StringBuilder();
                while ((r = reader.read()) != -1) {
                    builder.append((char) r);
                }
                System.out.println(Thread.currentThread() + " 收到了来自服务端的消息： " + builder.toString());
                reader.close();
                inputStream.close();
            }

            // close socket
            socket.close();
        }
    }


    private static final class Server {
        public static void main(String[] args) throws IOException, InterruptedException {
            // 如果要绑定的端口已经被占用（似乎只能检测出java程序产生的占用？），则抛出异常：java.net.BindException: Address already in use
            ServerSocket serverSocket = new ServerSocket(port);

            Runnable runnable = () -> {
                while (true) {
                    // 尝试与客户端建立连接：如果没有客户端连接此socket，则这里会阻塞，等待建立连接
                    try (Socket socket = serverSocket.accept();
                         InputStream inputStream = socket.getInputStream();
                         InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

                        int r;
                        StringBuilder builder = new StringBuilder();
                        while ((r = inputStreamReader.read()) != -1) {
                            builder.append((char) r);
                        }
                        System.out.println(Thread.currentThread() + " 收到了来自客户端的消息： " + builder.toString());

                        OutputStream outputStream = socket.getOutputStream();
                        outputStream.write("这是一条来自Socket服务端的消息\n".getBytes(StandardCharsets.UTF_8));
                        outputStream.flush();
                        // 结束输出
                        socket.shutdownOutput();
                        //outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            Thread thread1 = new Thread(runnable);
            thread1.start();

            Thread thread2 = new Thread(runnable);
            thread2.start();

            thread1.join();
            thread2.join();

            // close serverSocket
            serverSocket.close();
        }
    }

}
