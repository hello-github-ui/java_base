package com.example.service.impl;

import com.example.service.UserService;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

/**
 * Created by 19921224 on 2024/11/14 22:58
 */
@Service
public class UserServiceImpl implements UserService {

    @Override
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 10000))
    public int test(int code) throws Exception {
        System.out.println("test被调用, 时间: " + LocalTime.now());
        if (code == 0) {
            throw new Exception("情况不太对");
        }

        return 200;
    }

    /**
     * 可以看到传参里面写的是 Exception，这个是作为回调的接头暗号（重试次数用完了，还是失败，我们跑出这个Exception e
     * 通知触发这个回调方法）。
     * 对于 @Recover 注解的方法，需要特别注意的是：
     * 1、方法的返回值必须与 @Retryable 方法一致
     * 2、方法的第一个参数，必须是Throwable类型的，建议是与@Retryable配置的异常一样，其他的参数，需要哪个参数，写进去就可以了（
     *
     * @param e
     * @return
     * @Recover方法中有的） 3、该回调方法与重试方法写在同一个实现类里面
     * <p>
     * 注意事项：
     * 由于是基于AOP实现的，所以不支持类里面自调用方法。
     * 如果重试失败需要给@Recover注解的方法做后续处理，那这个重试的方法不能有返回值，只能是void。
     * 方法内不能使用 try-catch，只能往外跑出异常@Recover注解来开启重试失败后调用的方法（
     * 注意：需要跟重处理方法在同一个类中），此注解注释的方法参数一定要是@Retryable抛出的异常，
     * 否则无法识别，可以在该方法中进行日志处理。
     */
    @Recover
    public int recover(Exception e) {
        System.out.println("recover 方法被执行了...");
        return 400;
    }
}
