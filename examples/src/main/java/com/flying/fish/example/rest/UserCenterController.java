package com.flying.fish.example.rest;

import com.flying.fish.example.feign.OpenFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @Description 演示示例，模拟《用户管理系统》接口
 * @Author JL
 * @Date 2020/12/30
 * @Version V1.0
 */
@RestController
@RequestMapping("/userCenter")
public class UserCenterController {

    @Value("${server.port}")
    private String port;

    @Autowired
    private OpenFeignService openFeignService;

    /**
     * 提供外部调用API
     * @return
     */
    @RequestMapping(value = "/addUser")
    public String addUser() {
        return "server port: " + port + ",ok:" + System.currentTimeMillis();
    }

    /**
     * 提供外部调用API
     * @return
     */
    @RequestMapping(value = "/getUser")
    public String getUser() {
        return "server port: " + port + ",ok:" + System.currentTimeMillis();
    }

    /**
     * 提供外部调用API
     * @return
     */
    @RequestMapping(value = "/delUser")
    public String delUser() {
        return "server port: " + port + ",ok:" + System.currentTimeMillis();
    }

    /**
     * 提供外部调用API
     * @return
     */
    @RequestMapping(value = "/regUser")
    public String regUser() {
        return "server port: " + port + ",ok:" + System.currentTimeMillis();
    }

    /**
     * 提供外部调用API
     * @return
     */
    @RequestMapping(value = "/getToken")
    public String getToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * 提供外部调用API（结合Feign示例演示，http://127.0.0.1:8771/route/userCenter/feign/getUser）
     * @return
     */
    @RequestMapping(value = "/feign/getUser")
    public String getFeignUser() {
        return openFeignService.getUser();
    }

}
