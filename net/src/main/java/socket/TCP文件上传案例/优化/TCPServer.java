package socket.TCP文件上传案例.优化;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 030
 * @date 14:14 2021/11/9
 * @description 文件上传案例的服务器端，读取客户端上传的文件，保存到服务器，给客户端回写“上传成功”
 * <p>
 * 明确：
 * 数据源：客户端上传的文件
 * 目的地：服务器的硬盘 e:/temp-files/1-bak.jpg
 * <p>
 * 实现步骤：
 * 1. 创建一个服务器 ServerSocket 对象， 向系统申请指定的端口号
 * 2. 使用 ServerSocket 对象中的 accept 方法，获取到请求中客户端 Socket 连接
 * 3. 使用 Socket 对象中的 getInputStream 方法，获取到网络字节输入流 InputStream 对象
 * 4. 判断 e:/temp-files/1-bak.jpg 目录是否存在，不存在则创建
 * 5. 创建一个本地字节输出流 FileOutputStream 对象，构造方法中绑定要输出的目的地
 * 6. 使用网络字节输入流 InputStream 对象中的 read 方法，读取客户端上传到文件
 * 7. 使用本地字节输出流 FileOutputStream 对象中的 write 方法，把读取到的文件保存到服务器的硬盘上
 * 8. 使用 Socket 对象中的 getOutputStream 方法，获取到网络字节输出流 OutputStream 对象
 * 9. 使用网络字节输出流 OutputStream 对象中的 write 方法，给客户端回写“上传成功”
 * 10. 释放资源（FileOutputStream, Socket, ServerSocket）
 */
public class TCPServer {
    public static void main(String[] args) throws IOException {
        // 服务端端口号
        int tcpPort = 8888;
        // 服务器保存到硬盘的地址
        String folderPath = "e:/temp-files/upload/";

        // 1. 创建一个服务器 ServerSocket 对象， 向系统申请指定的端口号
        ServerSocket server = new ServerSocket(tcpPort);

        while (true) {
            // 2. 使用 ServerSocket 对象中的 accept 方法，获取到请求中客户端 Socket 连接
            Socket socket = server.accept();
            /*
            使用多线程来提高效率
            使用 匿名接口实现类
             */

            /**
             * 线程池来实现
             */
            ExecutorService es = Executors.newFixedThreadPool(10);
            es.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 3. 使用 Socket 对象中的 getInputStream 方法，获取到网络字节输入流 InputStream 对象
                        InputStream is = socket.getInputStream();

                        File folder = new File(folderPath);
                        //  4. 判断 e:/temp-files/tcp文件上传测试/ 目录是否存在，不存在则创建
                        if (!folder.exists() && !folder.isDirectory()) {
                            folder.mkdirs();
                        }

                        // 文件名规则 ip + 毫秒数 + 随机数
                        String uploadName = socket.getInetAddress().getHostAddress() + "-" +
                                Instant.now().getEpochSecond() + "-" +
                                new Random().nextInt(99999) + ".jpg";

                        // 5. 创建一个本地字节输出流 FileOutputStream 对象，构造方法中绑定要输出的目的地
                        FileOutputStream fos = new FileOutputStream(folderPath + uploadName);
                        // 6. 使用网络字节输入流 InputStream 对象中的 read 方法，读取客户端上传到文件
                        int len;
                        byte[] bytes = new byte[1024];
                        while ((len = is.read(bytes)) != -1) {   // is.read(bytes)读取客户端上传的文件，永远也读不到文件的结束标记，read方法进入到阻塞状态
                            // 7. 使用本地字节输出流 FileOutputStream 对象中的 write 方法，把读取到的文件保存到服务器的硬盘上
                            fos.write(bytes, 0, len);
                        }

                        /*那怎么解决这个阻塞呢？很简单，只需要在客户端发送消息时，写入一个标志，告诉服务端发送完消息了*/

                        // 8. 使用 Socket 对象中的 getOutputStream 方法，获取到网络字节输出流 OutputStream 对象
                        OutputStream os = socket.getOutputStream();
                        // 9. 使用网络字节输出流 OutputStream 对象中的 write 方法，给客户端回写“上传成功”
                        os.write("上传成功".getBytes(StandardCharsets.UTF_8));

                        System.out.printf("文件已保存至: %s%s", folderPath, uploadName);

                        // 10. 释放资源（FileOutputStream, Socket, ServerSocket）
                        fos.close();
                        socket.close();
                        // server.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }
}
