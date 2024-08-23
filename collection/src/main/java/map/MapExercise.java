package map;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class MapExercise {

    /**
     * 测试 Map 转换函数
     */
    @Test
    public void testMapTransform() {
        List<Integer> list = new LinkedList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);

//        List<Integer> collect = list.stream().map(new FunctionImpl()).collect(Collectors.toList());
//        collect.forEach(System.out::println);

        list.stream().map(x -> x * 2).collect(Collectors.toList()).forEach(System.out::println);
    }

//    class FunctionImpl implements Function<Integer, Integer>{
//
//        @Override
//        public Integer apply(Integer i) {
//            return i * 2;
//        }
//    }

    public static void main(String[] args) {
        // 测试 java 中是 值传递（值传递也叫 拷贝传递）
        List<String> shenzhen = new ArrayList<>();
        shenzhen.add("福田");
        shenzhen.add("宝安");
        List<String> guangzhou = shenzhen;

        shenzhen.forEach(System.out::println);
        System.out.println("==============");
        guangzhou.forEach(System.out::println);

        System.out.println("--------------删除之后---------");
        Iterator<String> iterator = guangzhou.iterator();
        while (iterator.hasNext()){
            if (iterator.next().equals("福田")){
                iterator.remove();
            }
        }
        shenzhen.forEach(System.out::println);
        System.out.println("==============");
        guangzhou.forEach(System.out::println);
        /*
        测试结果：
        福田
        宝安
        ==============
        福田
        宝安
        --------------删除之后---------
        宝安
        ==============
        宝安
         */


        // 结论：java中是值传递，当是引用对象时，该值指的是 引用地址值 的传递
        // 所以：如果两个声明的引用变量 都指向 同一个 堆地址值，其中一个修改了堆中的内容的话，两个变量指向的内容都会发生改变
    }
}
