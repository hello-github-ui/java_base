package exec.day01;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author 030
 * @date 11:18 2021/11/8
 * @description HOW TO FIND A UNIQUE NUMBER IN A LIST CONTAINING PAIRS?
 * 问题来源：https://yonatankra.com/how-to-find-a-unique-number-in-a-list-of-pairs/
 * 要求如下：
 *  给定数组：[1,3,17,3,1]
 *  返回只出现一次的元素17
 */
public class FindUniqueNum {

    // 给定原始数组
    private static int[] array = {1, 3, 17, 3, 2, 4, 6, 17, 2, 8, 6, 1};

    @org.junit.jupiter.api.Test
    public void test(){
        Integer[] results = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
            boolean found = false;
            for (int j = 0; j < array.length; j++) {
                if (array[i] == array[j] && i != j) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                results[i] = (array[i]);
            }
        }

        // 输出 只出现一次的元素
        Arrays.stream(results).filter(Objects::nonNull).forEach(System.out::println);
    }
}
