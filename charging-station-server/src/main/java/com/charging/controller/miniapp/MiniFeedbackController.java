package com.charging.controller.miniapp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.common.result.R;
import com.charging.entity.Feedback;
import com.charging.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "小程序端-反馈接口")
@RestController
@RequestMapping("/miniapp/feedback")
@RequiredArgsConstructor
public class MiniFeedbackController {

    private final FeedbackService feedbackService;

    @Operation(summary = "提交故障反馈")
    @PostMapping("/submit")
    public R<Void> submit(@RequestAttribute Long userId, @RequestBody Map<String, Object> params) {
        Feedback feedback = new Feedback();
        feedback.setUserId(userId);
        if (params.containsKey("type")) {
            feedback.setType(Integer.valueOf(params.get("type").toString()));
        }
        if (params.containsKey("stationId")) {
            feedback.setStationId(Long.valueOf(params.get("stationId").toString()));
        }
        String content = (String) params.get("content");
        if (content == null || content.trim().isEmpty()) {
            return R.error("反馈内容不能为空");
        }
        feedback.setContent(content.trim());
        if (params.containsKey("images")) {
            feedback.setImages((String) params.get("images"));
        }
        feedback.setStatus(0);
        feedbackService.save(feedback);
        return R.ok();
    }

    @Operation(summary = "获取用户反馈列表")
    @GetMapping("/list")
    public R<Page<Feedback>> list(
            @RequestAttribute Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Feedback::getUserId, userId);
        wrapper.orderByDesc(Feedback::getCreateTime);
        return R.ok(feedbackService.page(new Page<>(pageNum, pageSize), wrapper));
    }
}
