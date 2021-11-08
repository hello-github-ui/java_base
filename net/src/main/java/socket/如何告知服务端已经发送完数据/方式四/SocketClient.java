package socket.如何告知服务端已经发送完数据.方式四;

import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * @author 030
 * @date 0:52 2021/11/9
 * @description
 * 如何告知服务端已经发送完信息
 *  实现方式第四种：4、通过指定长度
 */
public class SocketClient {

    public static void main(String args[]) throws Exception {
        // 要连接的服务端IP地址和端口
        String host = "127.0.0.1";
        int port = 55533;
        // 与服务端建立连接
        Socket socket = new Socket(host, port);
        // 建立连接后获得输出流
        OutputStream os = socket.getOutputStream();
        String message = "你好服务端";
        //首先需要计算得知消息的长度
        byte[] sendBytes = message.getBytes(StandardCharsets.UTF_8);
        //然后将消息的长度优先发送出去
        os.write(sendBytes.length >> 8);
        os.write(sendBytes.length);
        //然后将消息再次发送出去
        os.write(sendBytes);
        os.flush();
        //==========此处重复发送一次，实际项目中为多个命名，此处只为展示用法
        message = "第二条消息|";
        sendBytes = message.getBytes(StandardCharsets.UTF_8);
        os.write(sendBytes.length >> 8);
        os.write(sendBytes.length);
        os.write(sendBytes);
        os.flush();
        //==========此处重复发送一次，实际项目中为多个命名，此处只为展示用法
        message = "the third message !";
        sendBytes = message.getBytes("UTF-8");
        os.write(sendBytes.length >> 8);
        os.write(sendBytes.length);
        os.write(sendBytes);

        os.close();
        socket.close();
    }
}

