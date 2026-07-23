package com.charging.controller.miniapp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charging.common.result.R;
import com.charging.entity.User;
import com.charging.service.UserService;
import com.charging.utils.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Tag(name = "小程序端-用户接口")
@RestController
@RequestMapping("/miniapp/user")
@RequiredArgsConstructor
public class MiniUserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${wechat.miniapp.app-id}")
    private String appId;

    @Value("${wechat.miniapp.app-secret}")
    private String appSecret;

    @Value("${wechat.miniapp.mock-mode:false}")
    private Boolean mockMode;

    @Operation(summary = "微信一键登录")
    @PostMapping("/wx-login")
    public R<Map<String, Object>> wxLogin(@RequestBody Map<String, String> params) {
        String code = params.get("code");
        if (code == null || code.isEmpty()) {
            return R.error("微信登录code不能为空");
        }

        String openid;
        try {
            if (Boolean.TRUE.equals(mockMode)
                    || appId == null || appId.isBlank() || "your-app-id".equals(appId)
                    || appSecret == null || appSecret.isBlank() || "your-app-secret".equals(appSecret)) {
                // 开发/mock 模式：用 code 模拟 openid，无需调用微信接口
                if (Boolean.TRUE.equals(mockMode)) {
                    log.warn("微信登录处于 mock 模式，使用 code 模拟 openid");
                } else {
                    log.error("微信小程序 app-id 或 app-secret 未配置");
                    return R.error("微信登录未配置，请联系管理员");
                }
                openid = "mock_" + code;
            } else {
                // 1. 用 code 换取 openid
                String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId
                        + "&secret=" + appSecret
                        + "&js_code=" + code
                        + "&grant_type=authorization_code";

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                String responseBody;
                try (Response response = client.newCall(request).execute()) {
                    responseBody = response.body() != null ? response.body().string() : "";
                }

                log.info("微信 jscode2session 响应: {}", responseBody);

                JsonNode jsonNode = objectMapper.readTree(responseBody);
                openid = jsonNode.has("openid") ? jsonNode.get("openid").asText() : null;
                String errMsg = jsonNode.has("errmsg") ? jsonNode.get("errmsg").asText() : null;
                Integer errCode = jsonNode.has("errcode") ? jsonNode.get("errcode").asInt() : null;

                if (openid == null) {
                    log.error("微信登录换取openid失败, errcode: {}, errmsg: {}", errCode, errMsg);
                    return R.error("微信登录失败: " + (errMsg != null ? errMsg : "请重试"));
                }
            }

            // 2. 根据 openid 查找或创建用户
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getOpenid, openid);
            User user = userService.getOne(wrapper);

            if (user == null) {
                user = new User();
                user.setOpenid(openid);
                user.setUsername("wx_" + openid.substring(0, 8));
                user.setNickname("微信用户");
                user.setStatus(1);
                user.setBalance(new java.math.BigDecimal("0.00"));
                user.setCreateTime(LocalDateTime.now());
                user.setUpdateTime(LocalDateTime.now());
                userService.save(user);
            }

            // 3. 生成 token
            String token = jwtUtil.generateToken(user.getId(), user.getPhone() != null ? user.getPhone() : openid, 0);

            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("user", buildUserInfo(user));
            return R.ok(result);

        } catch (Exception e) {
            log.error("微信登录异常", e);
            return R.error("微信登录失败，请重试");
        }
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public R<Void> register(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        String password = params.get("password");
        String nickname = params.get("nickname");

        if (phone == null || phone.isEmpty()) {
            return R.error("手机号不能为空");
        }
        if (phone.length() != 11) {
            return R.error("手机号格式不正确");
        }
        if (password == null || password.length() < 6) {
            return R.error("密码不少于6位");
        }

        // 检查手机号是否已注册
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        if (userService.count(wrapper) > 0) {
            return R.error("该手机号已注册");
        }

        User user = new User();
        user.setUsername(phone);
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname != null && !nickname.isEmpty() ? nickname : "用户" + phone.substring(phone.length() - 4));
        user.setStatus(1);
        user.setBalance(new java.math.BigDecimal("0.00"));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userService.save(user);

        return R.ok();
    }

    @Operation(summary = "手机号+密码登录")
    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        String password = params.get("password");

        if (phone == null || phone.isEmpty()) {
            return R.error("手机号不能为空");
        }
        if (password == null || password.isEmpty()) {
            return R.error("密码不能为空");
        }

        // 查找用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        User user = userService.getOne(wrapper);

        if (user == null) {
            return R.error("账号未注册，请先注册");
        }

        // 校验密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return R.error("密码错误");
        }

        if (user.getStatus() != 1) {
            return R.error("账号已被禁用");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getPhone(), 0);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", buildUserInfo(user));
        return R.ok(result);
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/info")
    public R<Map<String, Object>> info(@RequestAttribute Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            return R.error("用户不存在");
        }
        return R.ok(buildUserInfo(user));
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/update")
    public R<Void> update(@RequestAttribute Long userId, @RequestBody Map<String, String> params) {
        User user = userService.getById(userId);
        if (user == null) {
            return R.error("用户不存在");
        }
        if (params.containsKey("nickname")) {
            user.setNickname(params.get("nickname"));
        }
        if (params.containsKey("avatar")) {
            user.setAvatar(params.get("avatar"));
        }
        user.setUpdateTime(LocalDateTime.now());
        userService.updateById(user);
        return R.ok();
    }

    private Map<String, Object> buildUserInfo(User user) {
        Map<String, Object> info = new HashMap<>();
        info.put("id", user.getId());
        info.put("nickname", user.getNickname());
        info.put("phone", user.getPhone());
        info.put("avatar", user.getAvatar());
        info.put("balance", user.getBalance());
        return info;
    }
}
