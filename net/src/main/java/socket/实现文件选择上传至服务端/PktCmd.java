package socket.实现文件选择上传至服务端;

/**
 * @author 030
 * @date 22:00 2021/11/9
 * @description
 */
public class PktCmd {
    public final static int PKT_AUTH = 1; // 认证报文
    public final static int PKT_AUTH_RES = 2; // 认证结果报文
    public final static int AUTH_FAIL = 200; // 认证失败
    public final static int AUTH_SUCCESS = 400; // 认证成功
}
