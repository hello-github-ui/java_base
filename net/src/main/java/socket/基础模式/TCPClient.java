package socket.基础模式;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/*
参考文章：https://www.codenong.com/cs106175296/
 */

/**
 * @author 030
 * @date 15:34 2021/11/8
 * @description TCP通信的客户端：向服务器发送连接请求，给服务器发送数据，读取服务器回写的数据
 * 表示客户端的类：
 * java.net.Socket：此类实现客户端套接字（也可以叫做“套接字”）。套接字是两台机器之间通信的端点。
 * 套接字：包含了IP地址和端口号的网络单位
 * 构造方法：
 * Socket(String host, int port) 创建一个流套接字并将其连接到指定主机上的指定端口号
 * 参数：
 * String host：服务器主机的名称/服务器的ip地址
 * int port：服务器的端口号
 * 成员方法：
 * OutputStream getOutputStream() 返回此套接字的输出流
 * InputStream getInputStream() 返回此套接字的输入流
 * void close() 关闭此套接字
 * <p>
 * 实现步骤：
 * 1. 创建一个客户端对象 Socket，构造方法绑定服务器的IP地址和端口号
 * 2. 使用 Socket 对象中的方法 getOutputStream() 获取网络字节输出流 OutputStream对象
 * 3. 使用网络字节输出流 OutputStream 对象中的方法 write，给服务器发送数据
 * 4. 使用 Socket 对象中的方法 getInputStream() 获取网络字节输入流 InputStream对象
 * 5. 使用网络字节输入流 InputStream对象中的方法 read，读取服务器返回的数据。
 * 6. 释放资源（Socket）
 * 注意：
 * 1. 客户端和服务器端进行交互，必须使用Socket中提供的网络流，不能使用自己创建的流对象
 * 2. 当我们创建客户端对象Socket的时候，就会去请求服务器，并与服务器经过3次握手建立链接通路
 * 这时如果服务器没有启动，那么就会抛出异常
 * 如果服务器已经启动，那么就可以进行交互了
 */
public class TCPClient {
    public static void main(String[] args) throws IOException {
        // 1. 创建一个客户端对象Socket，构造方法绑定服务器的IP地址和端口号
        Socket socket = new Socket("127.0.0.1", 8888);
        // 2. 使用Socket对象中的方法getOutputStream获取网络字节输出流OutputStream对象
        OutputStream os = socket.getOutputStream();
        // 3. 使用网络字节输出流OutputStream对象中的方法write，给服务器发送数据
        String message = "你好服务器，我是客户端";
        os.write(message.getBytes(StandardCharsets.UTF_8));

        /*********解决bug：服务端读取不到 len = -1 标识，会一直处在死循环等待状态ing**********/
        //通过shutdownOutput高速服务器已经发送完数据，后续只能接受数据
        socket.shutdownOutput();

        // 4. 使用Socket对象中的方法getInputStream获取网络字节输入流InputStream对象
        InputStream is = socket.getInputStream();
        int len; // 基本数据类型在方法内部（局部变量）时，可以省略初始化，会默认初始化的；但是作为成员变量时则不可以
        byte[] bytes = new byte[1024];
        // 用于记录 服务端回写 的数据
        StringBuilder sb = new StringBuilder(); // 注意：多线程时要使用 StringBuffer
        while ((len = is.read(bytes)) != -1) {
            // 5. 使用网络字节输入流InputStream对象中的方法read，读取服务器返回的数据。
            // 注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
            sb.append(new String(bytes, 0, len, StandardCharsets.UTF_8));
        }
        // 打印输出一下服务端回写的数据
        System.out.println("get message from server: " + sb);
        // 6. 释放资源（Socket）
        is.close();
        os.close();
        socket.close();
    }
}
