package com.charging.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.common.result.R;
import com.charging.entity.Notice;
import com.charging.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/notice")
@RequiredArgsConstructor
public class AdminNoticeController {

    private final NoticeService noticeService;

    @GetMapping("/list")
    public R<Page<Notice>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Notice::getCreateTime);
        return R.ok(noticeService.page(new Page<>(pageNum, pageSize), wrapper));
    }

    @GetMapping("/{id}")
    public R<Notice> detail(@PathVariable Long id) {
        Notice notice = noticeService.getById(id);
        if (notice == null || notice.getDeleted() == 1) return R.error("公告不存在");
        return R.ok(notice);
    }

    @PostMapping
    public R<Void> add(@RequestBody Notice notice) {
        notice.setDeleted(0);
        noticeService.save(notice);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody Notice notice) {
        noticeService.updateById(notice);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        Notice notice = noticeService.getById(id);
        if (notice == null) {
            return R.error("公告不存在");
        }
        LambdaUpdateWrapper<Notice> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Notice::getId, id).set(Notice::getDeleted, 1);
        noticeService.update(wrapper);
        return R.ok();
    }
}
