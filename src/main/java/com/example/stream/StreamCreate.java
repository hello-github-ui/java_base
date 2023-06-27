package com.example.stream;

import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Created by 19921224 on 2023/6/19 16:02
 * 流的创建
 */
public class StreamCreate {
    public static void main(String[] args) {
        // 1. 使用 Stream.of() 静态方法构造一个流
        Stream<String> song = Stream.of("gently", "down", "the", "stream");
        // 如果你是一个数组，也是同理，
        String[] letters = new String[]{"a", "b", "c"};
        Stream<String> letterStream = Stream.of(letters);

        // 2. 创建一个不包含任何元素的流
        Stream<Object> empty = Stream.empty();

        // 3. 创建一个常量值的流
        Stream<String> echos = Stream.generate(() -> "Echo");
        // 创建一个随机数的流（使用方法引用）
        Stream<Double> randoms = Stream.generate(Math::random);
        // 上面这个写法等价于下面的  // 使用 Lambda表示创建一个匿名实例对象
        Supplier<Double> supplier = () -> Math.random();
        Stream<Double> randoms2 = Stream.generate(supplier);

    }
}