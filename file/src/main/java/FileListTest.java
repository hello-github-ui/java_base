import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * @author 030
 * @date 14:30 2021/11/8
 * @description 统计某个目录下所有的文件及文件夹相关操作
 */
public class FileListTest {

    /******************************************罗列出某个指定目录下的所有文件夹及文件（包括子目录下的） start *****************************************/
    //获取层级的方法
    private static String getLevel(int level) {
        //A mutable sequence of characters.
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append("|--");
        }
        return sb.toString();
    }

    // 获取该目录下指定层级的文件及文件夹
    public static void getAllFiles(File dir, int level) {
        System.out.println(getLevel(level) + dir.getName());
        level++;
        File[] files = dir.listFiles();
        for (int j = 0; j < files.length; j++) {
            if (files[j].isDirectory()) {
                //这里面用了递归的算法
                getAllFiles(files[j], level);
            } else {
                System.out.println(getLevel(level) + files[j]);
            }
        }
    }

    @Test
    public void test1() {
        String docPath = "E:\\webp";
        File file = new File(docPath);
        getAllFiles(file, 0);
    }

    /******************************************罗列出某个指定目录下的所有文件夹及文件（包括子目录下的） end *****************************************/


    /**********************************修改上面方法，实现输出某个目录下，每个txt文件内的数字个数，汉字等上个demo的信息******************************************/
// 获取该目录下指定层级的文件及文件夹
    public static void getEachFileCnt(File dir, int level) {
//        System.out.println(getLevel(level) + dir.getName());
        level++;
        try {
            File[] files = dir.listFiles();
            for (int j = 0; j < files.length; j++) {
                if (files[j].isDirectory()) {
                    //这里面用了递归的算法
                    getEachFileCnt(files[j], level);
                } else {
//                    System.out.println(getLevel(level) + files[j]);
                    // 如果是文件，只选择 txt 文件
                    String fileName = files[j].getName();
                    String suffix = FilenameUtils.getExtension(fileName);   // 这里用到了 commons-io 依赖包
                    if ("txt".equalsIgnoreCase(suffix)) {
                        // 获取该文件的行数
                        FileReader fileReader = new FileReader(files[j]);
                        BufferedReader bufferedReader = new BufferedReader(fileReader);
                        String strLine = "";
                        long lineCnt = 0L;
                        while ((strLine = bufferedReader.readLine()) != null) {
                            lineCnt++;
                            // 当然你可以在这里统计该文件的汉字个数，空格个数，数字个数等，这里不做演示了，如有需要请看上文：
                        }
                        System.out.println("文件：" + files[j].getName() + " 的行数为：" + lineCnt);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test2() {
        String docPath = "E:\\webp";
        File file = new File(docPath);
        getEachFileCnt(file, 0);
    }
}
