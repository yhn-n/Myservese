package com.charging.controller.miniapp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charging.common.result.R;
import com.charging.entity.Charger;
import com.charging.entity.Station;
import com.charging.service.ChargerService;
import com.charging.service.StationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "小程序端-充电桩接口")
@RestController
@RequestMapping("/miniapp/charger")
@RequiredArgsConstructor
public class MiniChargerController {

    private final ChargerService chargerService;
    private final StationService stationService;

    @Operation(summary = "获取站点下的充电桩列表")
    @GetMapping("/list/{stationId}")
    public R<List<Charger>> listByStation(@PathVariable Long stationId) {
        LambdaQueryWrapper<Charger> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Charger::getStationId, stationId);
        wrapper.orderByAsc(Charger::getCode);
        return R.ok(chargerService.list(wrapper));
    }

    @Operation(summary = "根据编号查询充电桩")
    @GetMapping("/code/{code}")
    public R<Map<String, Object>> getByCode(@PathVariable String code) {
        LambdaQueryWrapper<Charger> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Charger::getCode, code);
        Charger charger = chargerService.getOne(wrapper);
        if (charger == null) {
            return R.error("充电桩不存在");
        }
        Station station = stationService.getById(charger.getStationId());
        Map<String, Object> result = new HashMap<>();
        result.put("charger", charger);
        result.put("station", station);
        return R.ok(result);
    }
}
