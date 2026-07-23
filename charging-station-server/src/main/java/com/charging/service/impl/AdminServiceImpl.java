package com.charging.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.charging.entity.Admin;
import com.charging.mapper.AdminMapper;
import com.charging.service.AdminService;
import com.charging.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin>
        implements AdminService {

    private final JwtUtil jwtUtil;

    @Override
    public Map<String, Object> login(String username, String password) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, username);
        Admin admin = getOne(wrapper);

        if (admin == null) {
            throw new RuntimeException("用户名不存在");
        }
        if (!new BCryptPasswordEncoder().matches(password, admin.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        if (admin.getStatus() != null && admin.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        String token = jwtUtil.generateToken(admin.getId(), admin.getUsername(), 1);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);

        Map<String, Object> adminInfo = new HashMap<>();
        adminInfo.put("id", admin.getId());
        adminInfo.put("username", admin.getUsername());
        adminInfo.put("realName", admin.getRealName());
        adminInfo.put("phone", admin.getPhone());
        result.put("admin", adminInfo);

        return result;
    }
}
