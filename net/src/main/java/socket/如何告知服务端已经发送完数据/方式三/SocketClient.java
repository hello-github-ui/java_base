package socket.如何告知服务端已经发送完数据.方式三;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * @author 030
 * @date 1:49 2021/11/9
 * @description 如何告知服务端已经发送完信息
 * * 实现方式第四种：3、通过约定符号
 */
public class SocketClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 9999);
        OutputStream os = socket.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
        BufferedWriter bufferedWriter = new BufferedWriter(osw);
        String str = "你好，这是我的第一个socket，end";
        System.out.println(222);
        bufferedWriter.write(str);
        // 假如约定单端的一行为end，代表发送完成
        System.out.println(333);
        bufferedWriter.flush();
        System.out.println(4444);
    }
}
