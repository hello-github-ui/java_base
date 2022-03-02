package exec.day01;

/**
 * 打印 九九乘法表
 */
public class ArithmeticTable {
    public static void main(String[] args) {
        // 预先设置一个9*9满屏画布
        String[][] array = new String[9][9];
        for (int i = 1; i < 10; i++) {
            for (int j = 1; j < 10; j++) {
                array[i - 1][j - 1] = i + "*" + j + "=" + (i * j);
            }
        }

        String[][] newArray = new String[9][9];
        // 去掉不需要的元素
//        int length = array.length;
        for (int i = 1; i < 10; i++) {
            for (int j = 1; j < 10; j++) {
                if (i >= j) {
                    newArray[i - 1][j - 1] = i + "*" + j + "=" + (i * j);
                }
            }
        }

        for (String[] strings : newArray) {
            for (String string : strings) {
                if (string != null) {
                    System.out.print(string + "\t");
                }
            }
            System.out.println();
        }
    }
}
