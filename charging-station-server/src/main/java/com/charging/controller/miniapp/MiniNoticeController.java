package com.charging.controller.miniapp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.common.result.R;
import com.charging.entity.Notice;
import com.charging.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "小程序端-公告接口")
@RestController
@RequestMapping("/miniapp/notice")
@RequiredArgsConstructor
public class MiniNoticeController {

    private final NoticeService noticeService;

    @Operation(summary = "获取公告列表")
    @GetMapping("/list")
    public R<Page<Notice>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notice::getStatus, 1);
        wrapper.orderByDesc(Notice::getCreateTime);
        return R.ok(noticeService.page(new Page<>(pageNum, pageSize), wrapper));
    }

    @Operation(summary = "获取公告详情")
    @GetMapping("/{id}")
    public R<Notice> detail(@PathVariable Long id) {
        Notice notice = noticeService.getById(id);
        if (notice == null) {
            return R.error("公告不存在");
        }
        return R.ok(notice);
    }
}
