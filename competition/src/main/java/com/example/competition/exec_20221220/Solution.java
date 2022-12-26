package com.example.competition.exec_20221220;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * <a>https://leetcode.cn/problems/shu-zu-zhong-chu-xian-ci-shu-chao-guo-yi-ban-de-shu-zi-lcof/</a>
 * 剑指 Offer 39. 数组中出现次数超过一半的数字
 * 数组中有一个数字出现的次数超过数组长度的一半，请找出这个数字。
 * 你可以假设数组是非空的，并且给定的数组总是存在多数元素。
 * 示例 1:
 * 输入: [1, 2, 3, 2, 2, 2, 5, 4, 2]
 * 输出: 2
 * 限制：
 * 1 <= 数组长度 <= 50000
 */
public class Solution {

    public static void main(String[] args) {
        List<Integer> resultList = getNum(new int[]{5, 2, 5, 2, 2, 2, 5, 5, 2, 5});
        System.out.println("数组中出现次数超过一半的数字有：" + resultList);
    }

    public static List<Integer> getNum(int[] array) {
        // length
        int len = array.length;
        // total count
        int cnt = 1;
        // get number
        List<Integer> resultList = new ArrayList<>();
        // int[] => Integer[]
        List<Integer> asList = new ArrayList<>(len);
        for (int i : array) {
            asList.add(i);
        }
        // sort
        Collections.sort(asList);
//        asList.sort((i1, i2) -> {
//            return i1 - i2;
//        });
        // loop
        for (int i = 0; i < len; i++) {
            for (int j = i + 1; j < len; j++) {
                if (Objects.equals(asList.get(i), asList.get(j))) {
                    cnt++;
                    if (cnt >= len / 2) {
                        resultList.add(asList.get(i));
                        break;
                    }
                } else {
                    break;
                }
            }
            cnt = 1;
        }
        return resultList;
    }
}
