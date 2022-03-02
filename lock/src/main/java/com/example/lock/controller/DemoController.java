package com.example.lock.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class DemoController {

    private final Logger logger = LoggerFactory.getLogger(DemoController.class);

    @RequestMapping("/test")
    @ResponseBody
    public String test() {
        return "springboot 访问测试，起飞，飞飞飞飞 ~ ~ ~";
    }

//    @RequestMapping("/process/{orderId}")
//    @ResponseBody
//    public Map<String, Object> process(@PathVariable("orderId") String orderId) throws Exception {
//        synchronized (this) {  // 锁 当前请求对象
//            logger.debug("[{}] 开始", orderId);
//            Thread.sleep(1500);
//            logger.debug("[{}] 结束", orderId);
//        }
//        Map<String, Object> map = new HashMap();
//        map.put("result", "success");
//        return map;
//    }

//    @RequestMapping("/process/{orderId}")
//    @ResponseBody
//    public Map<String, Object> process(@PathVariable("orderId") String orderId) throws Exception {
//        synchronized (orderId.intern()) {   // 字符串转换为 字符串常量池值
//            logger.debug("[{}] 开始", orderId);
//            Thread.sleep(1500);
//            logger.debug("[{}] 结束", orderId);
//        }
//        Map<String, Object> map = new HashMap();
//        map.put("result", "success");
//        return map;
//    }

    // 缓存锁
//    Map<String, Object> mutexCache = new HashMap<>();

//    @RequestMapping("/process/{orderId}")
//    @ResponseBody
//    public Map<String, Object> process(@PathVariable("orderId") String orderId) throws Exception {
//        Object mute4Key = mutexCache.get(orderId);
//        if (mute4Key == null){
//            mute4Key = new Object();
//            mutexCache.put(orderId, mute4Key);
//        }
//        synchronized (mute4Key) {   // 锁对象是 缓存中对应的 value
//            logger.debug("[{}] 开始", orderId);
//            Thread.sleep(1500);
//            logger.debug("[{}] 结束", orderId);
//            // 业务执行完，删除缓存
//            mutexCache.remove(orderId);
//        }
//        Map<String, Object> map = new HashMap();
//        map.put("result", "success");
//        return map;
//    }

//    @RequestMapping("/process/{orderId}")
//    @ResponseBody
//    public Map<String, Object> process(@PathVariable("orderId") String orderId) throws Exception {
//        Object mute4Key = null;
//        synchronized (this){
//            mute4Key = mutexCache.get(orderId);
//            if (mute4Key == null){
//                mute4Key = new Object();
//                mutexCache.put(orderId, mute4Key);
//            }
//        }
//
//        synchronized (mute4Key) {   // 锁对象是 缓存中对应的 value
//            logger.debug("[{}] 开始", orderId);
//            Thread.sleep(1500);
//            logger.debug("[{}] 结束", orderId);
//            // 业务执行完，删除缓存
//            mutexCache.remove(orderId);
//        }
//        Map<String, Object> map = new HashMap();
//        map.put("result", "success");
//        return map;
//    }

    // 缓存锁
    Map<String, Object> mutexCache = new ConcurrentHashMap<>(); // ConcurrentHashMap 线程安全的
    @RequestMapping("/process/{orderId}")
    @ResponseBody
    public Map<String, Object> process(@PathVariable("orderId") String orderId) throws Exception {
        Object mute4Key = mutexCache.computeIfAbsent(orderId, k -> new Object());

        synchronized (mute4Key) {   // 锁对象是 缓存中对应的 value
            logger.debug("[{}] 开始", orderId);
            Thread.sleep(1500);
            logger.debug("[{}] 结束", orderId);
            // 业务执行完，删除缓存
            mutexCache.remove(orderId);
        }
        Map<String, Object> map = new HashMap();
        map.put("result", "success");
        return map;
    }
}
