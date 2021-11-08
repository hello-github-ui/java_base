package socket.如何告知服务端已经发送完数据.方式三;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * @author 030
 * @date 1:44 2021/11/9
 * @description 如何告知服务端已经发送完信息
 * 实现方式第四种：3、通过约定符号
 */
public class SocketServer {

    public static void main(String[] args) throws Exception {
        // 初始化服务端socket并且绑定9999端口
        ServerSocket serverSocket = new ServerSocket(9999);
        // 等待客户端的连接
        Socket socket = serverSocket.accept();
        // 获取socket的网络输入流
        InputStream is = socket.getInputStream();
        // 构造一个本地字符输入流（读取流）
        InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
        // 获取输入流
        BufferedReader bufferedReader = new BufferedReader(isr);
        // 每行内容
        String line;
        System.out.println(111);
        StringBuilder sb = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null && !"end".equals(line)){
            //注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
            sb.append(line);
        }
        // 输出打印
        System.out.println(sb);
        System.out.println(5555);
    }
}
