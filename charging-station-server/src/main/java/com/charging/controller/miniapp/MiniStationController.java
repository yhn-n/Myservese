package com.charging.controller.miniapp;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.common.result.R;
import com.charging.entity.Station;
import com.charging.service.StationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@Tag(name = "小程序端-充电站接口")
@RestController
@RequestMapping("/miniapp/station")
@RequiredArgsConstructor
public class MiniStationController {
    private final StationService stationService;
    @Operation(summary = "获取充电站列表")
    @GetMapping("/list")
    public R<Page<Station>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer
                    pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10")
            Integer pageSize,
            @Parameter(description = "城市") @RequestParam(required = false) String
                    city,
            @Parameter(description = "关键字") @RequestParam(required = false) String
                    keyword) {

        LambdaQueryWrapper<Station> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Station::getStatus, 1);
        if (city != null && !city.isEmpty()) {
            wrapper.eq(Station::getCity, city);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Station::getName, keyword)
                    .or().like(Station::getAddress, keyword));
        }
        wrapper.orderByDesc(Station::getCreateTime);

        return R.ok(stationService.page(new Page<>(pageNum, pageSize), wrapper));
    }
    @Operation(summary = "获取充电站详情")
    @GetMapping("/{id}")
    public R<Station> detail(@PathVariable Long id) {
        Station station = stationService.getById(id);
        if (station == null) {
            return R.error("充电站不存在");
        }
        return R.ok(station);
    }
    @Operation(summary = "根据位置获取附近充电站")
    @GetMapping("/nearby")
    public R<?> nearby(
            @Parameter(description = "经度") @RequestParam Double longitude,
            @Parameter(description = "纬度") @RequestParam Double latitude,
            @Parameter(description = "范围(公里)") @RequestParam(defaultValue = "5")
            Double radius) {
        return R.ok(stationService.findNearbyStations(longitude, latitude, radius));
    }
}