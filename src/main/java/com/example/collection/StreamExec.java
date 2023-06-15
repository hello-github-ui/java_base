package com.example.collection;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 19921224 on 2023/6/15 11:06
 */
public class StreamExec {
    /*
    从迭代到流的操作
    在处理集合时，我们通常会迭代遍历它的元素，并在每个元素上执行某项操作。例如，假设我们想要对某本书中的所有单词进行计数。
    首先，将所有单词放到一个列表中：
     */
    public static void main(String[] args) throws IOException {
        // Read file into string
        String contents = new String(
                Files.readAllBytes(Paths.get("alice.txt")), StandardCharsets.UTF_8);
        // Split into words; nonletters are delimiters
        List<String> words = Arrays.asList(contents.split("\\PL+"));

        long count = 0;
        for (String word : words) {
            if (word.length() > 12) count++;
        }
        System.out.println("count: ${count}");
    }
}
