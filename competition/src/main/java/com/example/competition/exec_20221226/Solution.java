package com.example.competition.exec_20221226;

import java.util.Arrays;

/**
 * <a>https://leetcode.cn/problems/zui-xiao-de-kge-shu-lcof/</a>
 * 剑指 Offer 40. 最小的k个数
 * 输入整数数组 arr ，找出其中最小的 k 个数。例如，输入4、5、1、6、2、7、3、8这8个数字，则最小的4个数字是1、2、3、4。
 * 示例 1：
 * <p>
 * 输入：arr = [3,2,1], k = 2
 * 输出：[1,2] 或者 [2,1]
 * 示例 2：
 * <p>
 * 输入：arr = [0,1,2,1], k = 1
 * 输出：[0]
 */
public class Solution {

    public static void main(String[] args) {
        int[] leastNumbers = getLeastNumbers(new int[]{0, 1, 2, 1}, 1);
        Integer[] result = new Integer[leastNumbers.length];
        for (int i = 0; i < leastNumbers.length; i++) {
            result[i] = leastNumbers[i];
        }
        System.out.println(Arrays.asList(result));
    }

    public static int[] getLeastNumbers(int[] arr, int k) {
        // sort array as ascending
        Arrays.sort(arr);
        // take k
        int[] aim = new int[k];
        for (int i = 0; i < k; i++) {
            aim[i] = arr[i];
        }
        return aim;
    }
}
