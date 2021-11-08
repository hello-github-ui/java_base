package 代码执行结构;

import org.junit.jupiter.api.Test;

/**
 * @author 030
 * @date 14:57 2021/11/8
 * @description 考察 前++，后++，以及 finally 和 return 的理解
 */
public class PlusPosition {
    private static int method1(){
        int numX = 0, numY = 1;
        try {
            numX = 5 / numX;
            return numY;
        }catch (Exception e){
            return numY++;
        }finally {
            System.out.println(numY);   // 2
            return numY++;
        }
    }

    @Test
    public void test(){
        System.out.println(method1());  // 2
    }
}
