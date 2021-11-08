package com.example.controller;

import com.example.common.R;
import com.example.model.User;
import com.example.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class UserController {

    @Resource
    UserService userService;

    /*调用这个方法时，报错会被两个异常类都给捕获到*/
    @GetMapping("/getList")
    public Object getList(){
        List<User> users = userService.getList();
        return users;
    }


    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public R update(@RequestBody User user){
        userService.update(user);
        return R.success(null);
    }

    @GetMapping("/delete")
    public R delete(User user){
        userService.delete(user);
        return R.success(null);
    }


    @PostMapping("/hello")
    public Object hello(@RequestBody User user){
        userService.update(user);
        userService.delete(user);
        return "succeed";
    }
}
