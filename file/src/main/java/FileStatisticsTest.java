import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 030
 * @date 14:52 2021/11/8
 * @description 统计某个文件中目标内容的个数
 */
public class FileStatisticsTest {
    /**
     * 统计str中包含的数字
     *
     * @param str 目标字符串
     * @return 包含数字的个数
     */
    private static long getNumCnt(String str) {
        // 统计个数
        // 注意：基础数据类型作为普通变量（定义在方法内部的变量），不会进行初始化，直接应用时会报编译错误；如下代码，若是不初始化会报错
        // 当作为 成员变量 （类的成员变量）时，如果你只是声明并没有没有显式地进行初始化，这时Java会自动进行初始化操作，赋给默认值，
        // 注意：long 变量的默认值是 0L
        long cnt = 0L;
        // 正则匹配模式
        String regex = "\\d";
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(str);
        while (matcher.find()) {
            cnt++;
        }
        return cnt;
    }


    /**
     * 统计str中包含的空格个数
     *
     * @param str
     * @return
     */
    private static long getSpaceCnt(String str) {
        // 注意：基础数据类型作为普通变量（定义在方法内部的变量），不会进行初始化，直接应用时会报编译错误；如下代码，若是不初始化会报错
        // 当作为 成员变量 （类的成员变量）时，如果你只是声明并没有没有显式地进行初始化，这时Java会自动进行初始化操作，赋给默认值，
        // 注意：long 变量的默认值是 0L
        long cnt = 0L;
        String regex = "\\s";
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(str);
        while (matcher.find()) {
            cnt++;
        }
        return cnt;
    }

    /**
     * 统计str中包含的汉字个数
     *
     * @param str
     * @return
     */
    private static long getChineseCnt(String str) {
        // 注意：基础数据类型作为普通变量（定义在方法内部的变量），不会进行初始化，直接应用时会报编译错误；如下代码，若是不初始化会报错
        // 当作为 成员变量 （类的成员变量）时，如果你只是声明并没有没有显式地进行初始化，这时Java会自动进行初始化操作，赋给默认值，
        // 注意：long 变量的默认值是 0L
        long cnt = 0L;
        String regex = "[\\u4e00-\\u9fa5]";
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(str);
        while (matcher.find()) {
            cnt++;
        }
        return cnt;
    }

    /**
     * 统计str中包含的字母个数
     *
     * @param str
     * @return
     */
    private static long getLetterCnt(String str) {
        // 注意：基础数据类型作为普通变量（定义在方法内部的变量），不会进行初始化，直接应用时会报编译错误；如下代码，若是不初始化会报错
        // 当作为 成员变量 （类的成员变量）时，如果你只是声明并没有没有显式地进行初始化，这时Java会自动进行初始化操作，赋给默认值，
        // 注意：long 变量的默认值是 0L
        long cnt = 0L;
        String regex = "[a-zA-Z]";
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(str);
        while (matcher.find()) {
            cnt++;
        }
        return cnt;
    }

    /**
     * 统计str中包含的标点符号个数
     *
     * @param str
     * @return
     */
    private static long getPunctuationCnt(String str) {
        // 注意：基础数据类型作为普通变量（定义在方法内部的变量），不会进行初始化，直接应用时会报编译错误；如下代码，若是不初始化会报错
        // 当作为 成员变量 （类的成员变量）时，如果你只是声明并没有没有显式地进行初始化，这时Java会自动进行初始化操作，赋给默认值，
        // 注意：long 变量的默认值是 0L
        long cnt = 0L;
        String regex = "[\\pP\\p{Punct}]";
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(str);
        while (matcher.find()) {
            cnt++;
        }
        return cnt;
    }

    /**
     * 统计str中包含的任意字符个数（包括换行符，如果不想包括，只需修改正则即可）
     *
     * @param str
     * @return
     */
    private static long getCharCnt(String str) {
        // 注意：基础数据类型作为普通变量（定义在方法内部的变量），不会进行初始化，直接应用时会报编译错误；如下代码，若是不初始化会报错
        // 当作为 成员变量 （类的成员变量）时，如果你只是声明并没有没有显式地进行初始化，这时Java会自动进行初始化操作，赋给默认值，
        // 注意：long 变量的默认值是 0L
        long cnt = 0L;
        // 正则表达式中，使用.可以匹配任意字符，但是这其中不包含换行符\n
        // 要想使用正则表达式匹配包含换行符在内的任意字符，可以使用下面几种写法：
        // ([\s\S]*) 或者 ([\d\D]*) 或者 ([\w\W]*)
        String regex = ".";
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(str);
        while (matcher.find()) {
            cnt++;
        }
        return cnt;
    }


    /**
     * 测试：输出目标文件中包含多少行，多少个数字，多少个空格，多少个汉字， 多少个字母，多少个标点符号，多少个字符
     * 以 txt 文件为例
     */
    @Test   // 注意：单元测试方法不能有 返回值，因为单元测试是对每个独立功能的验证测试，有返回值，别的测试方法就可以调用了，这样不符合单元测试的初衷。
    public void testCnt() {
        String filePath = "E:\\webp\\test1\\2.txt";
        int lineCnt = 0, numCnt = 0, spaceCnt = 0, chineseCnt = 0, letterCnt = 0, punctuationCnt = 0, charCnt = 0;
        try {
            File file = new File(filePath);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String strLine = "";
            while ((strLine = bufferedReader.readLine()) != null) {
                // 行数
                lineCnt++;
                // 数字个数
                numCnt += getNumCnt(strLine);
                // 空格
                spaceCnt += getSpaceCnt(strLine);
                // 汉字
                chineseCnt += getChineseCnt(strLine);
                // 字母
                letterCnt += getLetterCnt(strLine);
                // 标点符号
                punctuationCnt += getPunctuationCnt(strLine);
                // 字符
                charCnt += getCharCnt(strLine);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("该文件总共： " + lineCnt + "行，" + numCnt + " 个数字，" + spaceCnt + " 个空格，" + chineseCnt + " 个汉字，"
                + letterCnt + " 个字母，" + punctuationCnt + " 个标点符号，" + charCnt + " 个字符。");
    }


    /**************************************************复习一下文件复制操作 start **************************************************/
    private void copyFile(File file) throws Exception {
        // 构造一个输入流，从硬盘读取内容到内存
        FileInputStream fis = new FileInputStream(file);
        String outputPath = "E:\\webp\\copy.txt";
        File file1 = new File(outputPath);
        FileOutputStream fos = new FileOutputStream(file1);
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = fis.read(buffer)) != -1) {
            fos.write(buffer, 0, len);
        }
        fos.close();
        fis.close();
        System.out.println("finished ...");
    }

    @Test
    public void test() throws Exception {
        String url = "E:\\webp\\test1\\1.txt";
        File file = new File(url);
        copyFile(file);
    }
    /**************************************************复习一下文件复制操作 end **************************************************/
}
