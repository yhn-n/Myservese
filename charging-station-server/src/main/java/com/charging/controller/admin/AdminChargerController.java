package com.charging.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.common.result.R;
import com.charging.entity.Charger;
import com.charging.service.ChargerService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin/charger")
@RequiredArgsConstructor
public class AdminChargerController {

    private final ChargerService chargerService;

    @GetMapping("/list")
    public R<Page<Charger>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long stationId,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Charger> wrapper = new LambdaQueryWrapper<>();
        if (stationId != null) {
            wrapper.eq(Charger::getStationId, stationId);
        }
        if (status != null) {
            wrapper.eq(Charger::getStatus, status);
        }
        wrapper.orderByDesc(Charger::getCreateTime);
        return R.ok(chargerService.page(new Page<>(pageNum, pageSize), wrapper));
    }

    @GetMapping("/{id}")
    public R<Charger> detail(@PathVariable Long id) {
        Charger charger = chargerService.getById(id);
        if (charger == null || charger.getDeleted() == 1) return R.error("充电桩不存在");
        return R.ok(charger);
    }

    @PostMapping
    public R<Void> add(@RequestBody Charger charger) {
        if (charger.getCode() == null || charger.getCode().isEmpty()) {
            charger.setCode("CHG" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        }
        charger.setDeleted(0);
        chargerService.save(charger);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody Charger charger) {
        chargerService.updateById(charger);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        Charger charger = chargerService.getById(id);
        if (charger == null) {
            return R.error("充电桩不存在");
        }
        LambdaUpdateWrapper<Charger> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Charger::getId, id).set(Charger::getDeleted, 1);
        chargerService.update(wrapper);
        return R.ok();
    }

    @GetMapping("/{id}/qrcode")
    public R<String> getQrcode(@PathVariable Long id) {
        Charger charger = chargerService.getById(id);
        if (charger == null) return R.error("充电桩不存在");

        try {
            String content = charger.getCode();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 2);

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, 300, 300, hints);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", baos);
            String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());
            return R.ok("data:image/png;base64," + base64);
        } catch (WriterException | IOException e) {
            return R.error("生成二维码失败");
        }
    }
}
