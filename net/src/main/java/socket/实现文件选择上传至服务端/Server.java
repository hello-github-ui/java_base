package socket.实现文件选择上传至服务端;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 030
 * @date 21:57 2021/11/9
 * @description
 */
public class Server {

    private JFrame frame;
    private JPasswordField txtPassword;
    private JLabel lblFileCount;
    private JButton btnChoosePath;
    private JButton btnOpenDir;
    private JButton btnStart;
    private JButton btnStop;
    private JTextArea textAreaRec;
    //
    String savePath;
    boolean bStart = false;
    int port = 9000;
    ServerSocket server;
    String passord = "12345";
    int numOfUploadFile = 0;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Server window = new Server();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public Server() {
        initialize();
        savePath = System.getProperty("user.dir");
        txtPassword.setText(passord);
    }

    public String getTimeStr() {
        SimpleDateFormat fm = new SimpleDateFormat("yy/MM/dd hh:mm:ss");
        return fm.format(new Date());
    }

    public void addMsgRec(String msg) {
        textAreaRec.append(msg + "\n");
        textAreaRec.setCaretPosition(textAreaRec.getText().length());
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setTitle("\u6587\u4EF6\u670D\u52A1\u5668");
        frame.setBounds(100, 100, 645, 419);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblNewLabel_1 = new JLabel(
                "\u8BBE\u7F6E\u4E0A\u4F20\u5BC6\u7801:");
        lblNewLabel_1.setBounds(14, 17, 98, 18);
        frame.getContentPane().add(lblNewLabel_1);

        txtPassword = new JPasswordField();
        txtPassword.setEchoChar('*');
        txtPassword.setBounds(115, 14, 115, 24);
        frame.getContentPane().add(txtPassword);

        btnStart = new JButton("\u542F\u52A8\u670D\u52A1\u5668");
        btnStart.setBounds(280, 48, 153, 27);
        frame.getContentPane().add(btnStart);


        btnStart.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                Thread t = new Thread(new ServerThread());
                t.start();
                btnStop.setEnabled(true);
                btnStart.setEnabled(false);
            }
        });

        btnStop = new JButton("\u505C\u6B62\u670D\u52A1\u5668");
        btnStop.setEnabled(false);
        btnStop.setBounds(465, 48, 143, 27);
        frame.getContentPane().add(btnStop);


        btnStop.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    bStart = false;
                    btnStart.setEnabled(true);
                    btnStop.setEnabled(false);
                    addMsgRec(getTimeStr() + "聊天室服务器已经关闭...");
                    server.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        btnChoosePath = new JButton(
                "\u9009\u62E9\u6587\u4EF6\u5B58\u653E\u8DEF\u5F84");
        btnChoosePath.setBounds(280, 13, 153, 27);
        frame.getContentPane().add(btnChoosePath);

        //
        btnChoosePath.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser dirChooser = new JFileChooser();
                dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (dirChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION)
                    savePath = dirChooser.getSelectedFile().getPath();
                addMsgRec(getTimeStr() + "上传的文件存储路径:" + savePath);
            }
        });

        btnOpenDir = new JButton("\u67E5\u770B\u5DF2\u4E0A\u4F20\u6587\u4EF6");
        btnOpenDir.setBounds(463, 13, 145, 27);
        frame.getContentPane().add(btnOpenDir);

        ///
        btnOpenDir.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                String command = "cmd /c start explorer" + savePath;
                try {
                    Runtime.getRuntime().exec(command);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        JLabel lblNewLabel_2 = new JLabel("\u6D88\u606F\u8BB0\u5F55");
        lblNewLabel_2.setBounds(14, 62, 72, 23);
        frame.getContentPane().add(lblNewLabel_2);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(14, 88, 594, 263);
        frame.getContentPane().add(scrollPane);

        textAreaRec = new JTextArea();
        textAreaRec.setFont(new Font("Monospaced", Font.PLAIN, 15));
        scrollPane.setViewportView(textAreaRec);

        lblFileCount = new JLabel(
                "\u76EE\u524D\u5DF2\u7ECF\u4E0A\u4F200\u4E2A\u6587\u4EF6");
        lblFileCount.setBounds(14, 354, 619, 18);
        frame.getContentPane().add(lblFileCount);
        // 更换样式
        try {
            String style = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
            UIManager.setLookAndFeel(style);
            // 更新窗体样式
            SwingUtilities.updateComponentTreeUI(this.frame);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //
    class ClientHandler implements Runnable {
        Socket s;
        String fileName;
        long fileLen;
        long byteOfRecv;
        boolean bAuthen = false;

        public ClientHandler(Socket s) {
            this.s = s;
        }

        public void auth_user() throws IOException {
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            DataInputStream dis = new DataInputStream(s.getInputStream());
            int pktType = dis.readInt();
            if (pktType == PktCmd.PKT_AUTH) {
                fileName = dis.readUTF();
                fileLen = dis.readLong();
                String pwd = dis.readUTF();

                if (pwd.equals(passord)) {
                    dos.writeInt(PktCmd.PKT_AUTH_RES);
                    dos.writeInt(PktCmd.AUTH_SUCCESS);
                    bAuthen = true;
                    addMsgRec("\n" + getTimeStr() + "客户端" + s.getRemoteSocketAddress() + "验证通过.");
                } else {
                    dos.writeInt(PktCmd.PKT_AUTH_RES);
                    dos.writeInt(PktCmd.AUTH_FAIL);
                    addMsgRec(getTimeStr() + "客户端" + s.getRemoteSocketAddress() + "验证失败.");
                }
                dos.flush();
            }
        }

        public void recv_file() throws IOException {
            long startTime = System.currentTimeMillis();
            BufferedInputStream bis = new BufferedInputStream(s.getInputStream());
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(savePath + "\\" + fileName));
            byteOfRecv = 0;
            byte[] recvBuf = new byte[4 * 1024];
            addMsgRec(getTimeStr() + "接收来自于" + s.getRemoteSocketAddress() + "的文件:\n" + fileName + ",共" + fileLen + "字节.");

            while (byteOfRecv < fileLen) {
                int len = bis.read(recvBuf);
                bos.write(recvBuf, 0, len);
                bos.flush();
                byteOfRecv += len;
            }
            bos.close();
            double spendTime = (System.currentTimeMillis() - startTime) / 1000.0;
            long rate = (long) (fileLen / spendTime);
            addMsgRec(getTimeStr() + "\n的文件" + fileName + "接收完成，共" + fileLen + "字节\n花费" + spendTime + "秒，传输速度为" + rate + "字节/秒" + ".(来自于" + s.getRemoteSocketAddress() + ")");
            numOfUploadFile++;
            lblFileCount.setText("目前已经上传" + numOfUploadFile + "个文件");
        }

        public void run() {
            try {
                auth_user();
                if (bAuthen)
                    recv_file();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    //
    class ServerThread implements Runnable {
        public void start_server() throws IOException {
            try {
                server = new ServerSocket(port);
                bStart = true;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(Server.this.frame, "服务器启动失败，请检查该端口是否被占用。");
                bStart = false;
                return;
            }
            addMsgRec(getTimeStr() + "聊天室服务器启动成功,在端口" + port + "侦听,默认密码为" + passord);
            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
            btnChoosePath.setEnabled(false);

            while (bStart) {
                Socket s = server.accept();
                Thread t = new Thread(new ClientHandler(s));
                t.start();
            }
        }

        public void run() {
            try {
                start_server();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
