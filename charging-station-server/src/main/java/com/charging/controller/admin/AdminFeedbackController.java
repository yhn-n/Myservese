package com.charging.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.common.result.R;
import com.charging.entity.Feedback;
import com.charging.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/feedback")
@RequiredArgsConstructor
public class AdminFeedbackController {

    private final FeedbackService feedbackService;

    @GetMapping("/list")
    public R<Page<Feedback>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Feedback::getStatus, status);
        }
        wrapper.orderByDesc(Feedback::getCreateTime);
        return R.ok(feedbackService.page(new Page<>(pageNum, pageSize), wrapper));
    }

    @GetMapping("/{id}")
    public R<Feedback> detail(@PathVariable Long id) {
        Feedback feedback = feedbackService.getById(id);
        if (feedback == null || feedback.getDeleted() == 1) return R.error("反馈不存在");
        return R.ok(feedback);
    }

    @PutMapping
    public R<Void> update(@RequestBody Feedback feedback) {
        feedbackService.updateById(feedback);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        Feedback feedback = feedbackService.getById(id);
        if (feedback == null) {
            return R.error("反馈不存在");
        }
        LambdaUpdateWrapper<Feedback> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Feedback::getId, id).set(Feedback::getDeleted, 1);
        feedbackService.update(wrapper);
        return R.ok();
    }
}
