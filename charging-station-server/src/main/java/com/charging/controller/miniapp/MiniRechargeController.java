package com.charging.controller.miniapp;

import com.charging.common.result.R;
import com.charging.entity.User;
import com.charging.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@Tag(name = "小程序端-充值接口")
@RestController
@RequestMapping("/miniapp/recharge")
@RequiredArgsConstructor
public class MiniRechargeController {

    private final UserService userService;

    @Operation(summary = "用户充值")
    @PostMapping
    public R<Void> recharge(@RequestAttribute Long userId, @RequestBody Map<String, Object> params) {
        User user = userService.getById(userId);
        if (user == null) {
            return R.error("用户不存在");
        }
        BigDecimal amount = new BigDecimal(params.get("amount").toString());
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return R.error("充值金额必须大于0");
        }
        user.setBalance(user.getBalance().add(amount));
        userService.updateById(user);
        return R.ok();
    }
}
