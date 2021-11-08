package socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/*
参考文章：https://www.codenong.com/cs106175296/
 */

/**
 * @author 030
 * @date 20:10 2021/11/8
 * @description TCP通信的服务器端：接收客户端的请求，读取客户端发送的数据，给客户端回写数据
 * 表示服务器的类：
 * java.net.ServerSocket：此类实现服务器套接字
 * <p>
 * 构造方法：
 * ServerSocket(int port) 创建绑定到特定端口的服务器套接字
 * 服务器端必须明确一件事，必须得知道是哪个客户端请求的服务器
 * 所以可以使用 accept() 方法获取到请求的客户端对象 Socket
 * 成员方法：
 * Socket accept() 侦听并接受到此套接字的连接
 * 服务器的实现步骤：
 * 1. 创建服务器 ServerSocket 对象和系统要指定的端口号
 * 2. 使用 ServerSocket 对象中的方法 accept， 获取到请求的客户端对象 Socket
 * 3. 使用Socket对象中的方法 getInputStream() 获取网络字节输入流 InputStream对象
 * 4. 使用网络字节输入流 InputStream 对象中的方法 read，读取客户端发送的数据
 * 5. 使用 Socket 对象中的方法 getOutputStream() 获取网络字节输出流OutputStream对象
 * 6. 使用网络字节输出流 OutputStream 对象中的方法 write，给客户端回写数据
 * 7. 释放资源（Socket， ServerSocket）
 */
public class TCPServer {

    /*一定要先启动 服务端，再启动 客户端才能执行*/
    public static void main(String[] args) throws IOException {
        // 1. 创建服务器 ServerSocket 对象和系统要指定的端口号
        ServerSocket server = new ServerSocket(8888);
        // 2. 使用 ServerSocket 对象中的方法 accept， 获取到请求的客户端对象 Socket
        Socket socket = server.accept();
        // 3. 使用Socket对象中的方法 getInputStream() 获取网络字节输入流 InputStream对象
        InputStream is = socket.getInputStream();
        int len;
        byte[] bytes = new byte[1024];
        StringBuilder sb = new StringBuilder(); // 多线程下注意异常
        // 只有当客户端关闭它的输出流的时候，服务端才能取得结尾的-1
        while ((len = is.read(bytes)) != -1) {
            // 4. 使用网络字节输入流 InputStream 对象中的方法 read，读取客户端发送的数据
            // 注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
            sb.append(new String(bytes, 0, len, StandardCharsets.UTF_8));
        }
        // 打印输出 客户端发送的消息
        System.out.println("get message from client: " + sb);

        // 5. 使用 Socket 对象中的方法 getOutputStream() 获取网络字节输出流OutputStream对象
        OutputStream os = socket.getOutputStream();
        // 6. 使用网络字节输出流 OutputStream 对象中的方法 write，给客户端回写数据
        os.write("Hello Client,I get the message...".getBytes(StandardCharsets.UTF_8));
        //  7. 释放资源（Socket， ServerSocket）
        is.close();
        os.close();
        socket.close();
        server.close();
    }
}
