package exec.day01;

import org.junit.jupiter.api.Test;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author 030
 * @date 13:38 2021/11/8
 * @description 将一个 int[] 转换为 Integer[]
 */
public class IntArray2Integer {

    // 初始化一个 基本数据类型的数组
    private static int[] primitiveArray = new int[]{1, 3, 17, 3, 2,  4, 6, 7, 2, 8, 6, 1};

    @Test
    public void test(){
        // 这里我们使用到一个工具类来转化 Apache common-lang3 utils ArrayUtils
        Integer[] boxedArray = ArrayUtils.toObject(primitiveArray);
        //也可以通过 for 循环遍历实现
        for (Integer integer : boxedArray) {
            System.out.println(integer);
        }
    }
}
