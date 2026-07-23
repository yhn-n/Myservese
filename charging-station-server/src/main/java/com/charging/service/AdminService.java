package com.charging.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.charging.entity.Admin;

import java.util.Map;

public interface AdminService extends IService<Admin> {

    Map<String, Object> login(String username, String password);
}
