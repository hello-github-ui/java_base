package socket.TCP文件上传案例;

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
        // 本地字节输入流对象
        FileInputStream fis = null;
        // 网络字节输入流
        InputStream is = null;
        // 网络字节输出流
        OutputStream os = null;
        // Socket对象
        Socket socket = null;
        try {
            // 1. 创建一个本地字节输入流FileInputStream对象，构造方法中绑定要读取的数据
            File file = new File("C:/Users/Administrator/Pictures/8k/1.jpg");
            fis = new FileInputStream(file);
            // 2. 创建一个客户端Socket对象，构造方法中绑定服务器的IP地址和端口号
            socket = new Socket("127.0.0.1", 8888);
            // 3. 使用Socket中的方法getOutputStream，获取网络字节输出流OutputStream对象
            os = socket.getOutputStream();
            int len;
            byte[] bytes = new byte[1024];
            // 4. 使用本地字节输入流FileInputStream对象中的read方法，读取本地文件
            while ((len = fis.read(bytes)) != -1) {
                // 5. 使用网络字节输出流OutputStream对象中的write方法，把读取到的文件上传到服务器
                os.write(bytes, 0, len);
            }
            // 6. 使用Socket中的方法getInputStream，获取网络字节输入流InputStream对象
            is = socket.getInputStream();
            StringBuilder sb = new StringBuilder();
            while ((len = is.read(bytes)) != -1) {
                // 7. 使用网络字节输入流InputStream对象中的read方法，读取服务器回写的数据
                sb.append(new String(bytes, 0, len, StandardCharsets.UTF_8));
            }
            // 打印输出
            System.out.println(sb);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null && socket != null) {
                try {
                    fis.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
