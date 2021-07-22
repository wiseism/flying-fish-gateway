package com.flying.fish.example.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description 生产者控制器
 * @Author jianglong
 * @Date 2020/04/23
 * @Version V1.0
 */
@RestController
@RequestMapping("/producer")
public class ProducerController {

    @Value("${server.port}")
    private String port;

    /**
     * 提供外部调用API
     * @return
     */
    @RequestMapping(value = "/info")
    public String info() {
        return "server port: " + port + ",ok:" + System.currentTimeMillis();
    }

    /**
     * 提供外部调用API
     * @return
     */
    @RequestMapping(value = "/test")
    public String test() {
        return "test api server port: " + port + ",ok:" + System.currentTimeMillis();
    }

    /**
     * 提供外部调用API
     * @return
     */
    @RequestMapping(value = "/test0")
    public String test0() {
        return "test0 api server port: " + port + ",ok:" + System.currentTimeMillis();
    }

    /**
     * 提供外部调用API
     * @return
     */
    @RequestMapping(value = "/test1")
    public String test1() {
        try {
            Thread.sleep(4 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "test1 api server port: " + port + ",ok:" + System.currentTimeMillis();
    }

}
