package com.example.stream;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
        /*
        1. 推荐使用InputStream替代任何它的子类（比如FileInputStream）进行开发。这么做能够让你的代码兼容任何类型而非某种确定类型的输入流。
        2. Java中的InputStream是不能重复读取的，它的读取是单向的，因为读取的时候，会有一个pos指针，它指示每次读取之后下一次要读取的起始位置，当读到最后一个字符的时候，pos指针不会重置。
        3. 如果想要重复使用InputStream对象，可以先把InputStream转化成ByteArrayOutputStream，后面要使用InputStream对象时，再从ByteArrayOutputStream转化回来就好了。
         */
        InputStream inputStream = StreamExec.class.getClassLoader().getResourceAsStream("alice.txt");
        // 所有的内容都读取到此数组之中
        byte[] bytes = new byte[1024];
        // 读取内容，存储到 bytes 缓存区中
        assert inputStream != null;
        inputStream.read(bytes);
        // 关闭输出流
        inputStream.close();
        // Read file into string
        String contents = new String(bytes, StandardCharsets.UTF_8);
        // Split into words; nonletters are delimiters
        List<String> words = Arrays.asList(contents.split("\\PL+"));

        long count = 0;
        for (String word : words) {
            if (word.length() > 12) count++;
        }
        System.out.printf("count: %s\n", count);

        // 在使用流时，相同的操作看起来像下面这样：
        count = words.stream()
                .filter(word -> word.length() > 12)
                .count();
        System.out.printf("count: %s\n", count);

        // 让流以并行的方式来执行
        count = words.parallelStream()
                .filter(word -> word.length() > 12)
                .count();
        System.out.printf("count: %s\n", count);

        /*
        流和集合的差异：
        1. 流并不存储其元素。这些元素可能存储在底层的集合中，或者是按需生成的。
        2. 流的操作不会修改其数据源。例如，filter方法不会从新的流中移除元素，而是会生成一个新的流，其中不包含被过滤掉的元素。
        3. 流的操作是尽可能惰性执行的。这意味着直至需要其结果时，操作才会执行。例如，如果我们只想查找前5个单词而不是所有长单词，那么filter方法就会在匹配到第5个单词后停止过滤。因此我们甚至可以操作无限流。
         */

        /*
        流操作时的典型流程：三阶段。
        1. 创建一个流。
        2. 指定将初始流转换为其它流的中间操作，可能包含多个步骤。
        3. 应用终止操作，从而产生结果。这个操作会强制执行之前的惰性操作。从此之后，这个流就再也不能用了。
        在上述流中，流是用stream或parallelStream方法创建的。filter方法对其进行转换，而count方法是终止操作。
         */
    }
}
