package socket.TCP文件上传案例.优化;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * @author 030
 * @date 12:10 2021/11/9
 * @description 文件上传案例的客户端，读取本地文件，上传到服务器，读取服务器回写的数据
 * <p>
 * 明确：
 * 数据源；C:/Users/Administrator/Pictures/8k/1.jpg
 * 目的地：服务器
 * 实现步骤：
 * 1. 创建一个本地字节输入流FileInputStream对象，构造方法中绑定要读取的数据
 * 2. 创建一个客户端Socket对象，构造方法中绑定服务器的IP地址和端口号
 * 3. 使用Socket中的方法getOutputStream，获取网络字节输出流OutputStream对象
 * 4. 使用本地字节输入流FileInputStream对象中的read方法，读取本地文件
 * 5. 使用网络字节输出流OutputStream对象中的write方法，把读取到的文件上传到服务器
 * 6. 使用Socket中的方法getInputStream，获取网络字节输入流InputStream对象
 * 7. 使用网络字节输入流InputStream对象中的read方法，读取服务器回写的数据
 * 8. 释放资源（FileInputStream, Socket）
 */
public class TCPClient {


    public static void main(String[] args) {
        // 服务器IP+Port
        String serverIP = "192.168.80.100"; // 我的虚拟机地址
        int tcpPort = 8888;
        Socket socket;
        String chooseFile = "C:/Users/Administrator/Pictures/8k/1.jpg";
        try {
            // 1. 创建一个本地字节输入流FileInputStream对象，构造方法中绑定要读取的数据
            File file = new File(chooseFile);
            FileInputStream fis = new FileInputStream(file);
            // 2. 创建一个客户端Socket对象，构造方法中绑定服务器的IP地址和端口号
            socket = new Socket(serverIP, tcpPort);
            // 3. 使用Socket中的方法getOutputStream，获取网络字节输出流OutputStream对象
            OutputStream os = socket.getOutputStream();
            int len;
            byte[] bytes = new byte[1024];
            // 4. 使用本地字节输入流FileInputStream对象中的read方法，读取本地文件
            while ((len = fis.read(bytes)) != -1) { // fis.read(bytes)读取客户端上传的文件，永远也读不到文件的结束标记，read方法进入到阻塞状态
                // 5. 使用网络字节输出流OutputStream对象中的write方法，把读取到的文件上传到服务器
                os.write(bytes, 0, len);
            }

            /*那怎么解决这个阻塞呢？很简单，只需要在客户端发送消息时，写入一个标志，告诉服务端发送完消息了*/
            socket.shutdownOutput();    // 告诉服务端，我已经发送完消息了

            // 6. 使用Socket中的方法getInputStream，获取网络字节输入流InputStream对象
            InputStream is = socket.getInputStream();
            StringBuilder sb = new StringBuilder();
            while ((len = is.read(bytes)) != -1) {
                // 7. 使用网络字节输入流InputStream对象中的read方法，读取服务器回写的数据
                sb.append(new String(bytes, 0, len, StandardCharsets.UTF_8));
            }
            // 打印输出
            System.out.println(sb);
            // 关闭流
            selfClose(fis, is, os);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 封装流的关闭
     */
    private static void selfClose(FileInputStream fis, InputStream is, OutputStream os) {
        // 本地字节输入流对象 FileInputStream
        // 网络字节输入流 InputStream
        // 网络字节输出流 OutputStream
        // Socket对象
        try {
            if (fis != null) fis.close();
            if (is != null) is.close();
            if (os != null) os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
