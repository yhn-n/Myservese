package com.charging.controller.admin;

import com.charging.common.result.R;
import com.charging.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        if (username == null || password == null) {
            return R.error("用户名和密码不能为空");
        }
        try {
            Map<String, Object> result = adminService.login(username, password);
            return R.ok(result);
        } catch (RuntimeException e) {
            return R.error(e.getMessage());
        }
    }
}
