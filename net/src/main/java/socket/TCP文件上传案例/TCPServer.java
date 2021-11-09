package socket.TCP文件上传案例;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

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
        // 1. 创建一个服务器 ServerSocket 对象， 向系统申请指定的端口号
        ServerSocket server = new ServerSocket(8888);
        // 2. 使用 ServerSocket 对象中的 accept 方法，获取到请求中客户端 Socket 连接
        Socket socket = server.accept();
        // 3. 使用 Socket 对象中的 getInputStream 方法，获取到网络字节输入流 InputStream 对象
        InputStream is = socket.getInputStream();
        String folderPath = "e:/temp-files/tcp文件上传测试/"; // 上传到的目的文件夹
        File folder = new File(folderPath);
        //  4. 判断 e:/temp-files/tcp文件上传测试/ 目录是否存在，不存在则创建
        if (!folder.exists() && !folder.isDirectory()) {
            folder.mkdirs();
            System.out.printf("%s 文件夹已经创建", folderPath);
        }
        // 5. 创建一个本地字节输出流 FileOutputStream 对象，构造方法中绑定要输出的目的地
        FileOutputStream fos = new FileOutputStream(folderPath + "1-bak.jpg");
        // 6. 使用网络字节输入流 InputStream 对象中的 read 方法，读取客户端上传到文件
        int len;
        byte[] bytes = new byte[1024];
        while ((len = is.read(bytes)) != -1) {
            // 7. 使用本地字节输出流 FileOutputStream 对象中的 write 方法，把读取到的文件保存到服务器的硬盘上
            fos.write(bytes, 0, len);
        }
        // 8. 使用 Socket 对象中的 getOutputStream 方法，获取到网络字节输出流 OutputStream 对象
        OutputStream os = socket.getOutputStream();
        // 9. 使用网络字节输出流 OutputStream 对象中的 write 方法，给客户端回写“上传成功”
        os.write("上传成功".getBytes(StandardCharsets.UTF_8));
        // 10. 释放资源（FileOutputStream, Socket, ServerSocket）
        fos.close();
        socket.close();
        server.close();

    }
}
