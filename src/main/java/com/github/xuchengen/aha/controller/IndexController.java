package com.github.xuchengen.aha.controller;

import com.github.xuchengen.aha.vo.OrderDO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class IndexController {

    private List<OrderDO> globalList = new ArrayList<>();

    /**
     * 堆内存溢出，全局变量未释放会造成其它业务无法申请内存空间持续性报堆内存溢出
     * 堆内存溢出，局部变量随着方法的退出而释放，对其他业务不会造成影响即便是有也只是短暂的
     */
    @GetMapping(value = {"/", "/index"})
    public OrderDO index() {
        OrderDO orderDO = new OrderDO();
        orderDO.setOrderNo("0000001");
        orderDO.setGoodsName("苹果笔记本2019款Pro版");
        orderDO.setAmount(new BigDecimal("14000"));
        return orderDO;
    }

    /**
     * 线程不断的写入数据到局部变量，堆溢出导致线程退出，局部变量会释放
     */
    @GetMapping(value = "/createOrder")
    public Date createOrder() {
        new Thread(() -> {
            System.out.println("create-order-#1的线程ID：" + Thread.currentThread().getId());
            List<OrderDO> list = new ArrayList<>();
            while (true) {
                OrderDO orderDO = new OrderDO();
                orderDO.setOrderNo("0000000000");
                orderDO.setGoodsName("Apple Mac Book Pro 2022");
                orderDO.setAmount(new BigDecimal("14000"));
                list.add(orderDO);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "create-order-#1").start();
        return new Date();
    }

    /**
     * 线程不断的写入数据到全局变量中，堆溢出导致线程退出，全局变量不会释放
     */
    @GetMapping("/createOrder2")
    public Date createOrder2() {
        new Thread(() -> {
            System.out.println("create-order-#2的线程ID：" + Thread.currentThread().getId());
            while (true) {
                OrderDO orderDO = new OrderDO();
                orderDO.setOrderNo("0000000000");
                orderDO.setGoodsName("Apple Mac Book Pro 2022");
                orderDO.setAmount(new BigDecimal("14000"));
                globalList.add(orderDO);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "create-order-#2").start();
        return new Date();
    }

    /**
     * CPU飙高
     */
    @GetMapping(value = "/fibonacci")
    public Date fibonacci() {
        new Thread(() -> {
            System.out.println("create-order-#2的线程ID：" + Thread.currentThread().getId());
            while (true) {
                for (int i = 0; i < 20; i++) {
                    fibonacci(i);
                }
            }
        }, "fibonacci-#1").start();
        return new Date();
    }

    private static long fibonacci(long number) {
        if ((number == 0) || (number == 1))
            return number;
        else
            return fibonacci(number - 1) + fibonacci(number - 2);
    }
}
