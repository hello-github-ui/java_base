package map;

import org.junit.jupiter.api.Test;

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
}
