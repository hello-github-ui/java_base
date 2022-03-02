import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class CopyDemo {
    public static void main(String[] args) throws Exception {
        // 1. 创建字节文件输入流
        FileInputStream fis = new FileInputStream(new File("E:\\lol\\1.mp4"));
        // 输出
        FileOutputStream fos = new FileOutputStream("E:\\lol\\2.mp4");
        byte[] bytes = new byte[1024];
        int len;
        while ((len = fis.read(bytes)) != -1) {
            fos.write(bytes, 0, len);
        }
        fos.close();
        fis.close();
        System.out.println("文件复制完成");
    }
}
