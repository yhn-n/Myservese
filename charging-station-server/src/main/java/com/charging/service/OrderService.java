package com.charging.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.charging.entity.Order;

import java.util.Map;

public interface OrderService extends IService<Order> {

    Map<String, Object> getStatistics();
}
