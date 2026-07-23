package com.charging.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.charging.common.result.R;
import com.charging.entity.Charger;
import com.charging.entity.Station;
import com.charging.service.ChargerService;
import com.charging.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/station")
@RequiredArgsConstructor
public class AdminStationController {

    private final StationService stationService;
    private final ChargerService chargerService;

    @GetMapping("/list")
    public R<Page<Station>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer status) {

        LambdaQueryWrapper<Station> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Station::getName, keyword)
                    .or().like(Station::getAddress, keyword));
        }
        if (city != null && !city.isEmpty()) {
            wrapper.eq(Station::getCity, city);
        }
        if (status != null) {
            wrapper.eq(Station::getStatus, status);
        }
        wrapper.orderByDesc(Station::getId);
        return R.ok(stationService.page(new Page<>(pageNum, pageSize), wrapper));
    }

    @GetMapping("/options")
    public R<List<Station>> options() {
        LambdaQueryWrapper<Station> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Station::getStatus, 1);
        wrapper.orderByAsc(Station::getId);
        return R.ok(stationService.list(wrapper));
    }

    @GetMapping("/{id:[0-9]+}")
    public R<Station> detail(@PathVariable Long id) {
        Station station = stationService.getById(id);
        if (station == null) {
            return R.error("充电站不存在");
        }
        return R.ok(station);
    }

    @PostMapping
    public R<Void> add(@RequestBody Station station) {
        stationService.save(station);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody Station station) {
        stationService.updateById(station);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        Station station = stationService.getById(id);
        if (station == null) {
            return R.error("充电站不存在");
        }
        LambdaUpdateWrapper<Station> stationWrapper = new LambdaUpdateWrapper<>();
        stationWrapper.eq(Station::getId, id).set(Station::getDeleted, 1);
        stationService.update(stationWrapper);

        // 级联删除该站点下的充电桩（逻辑删除）
        LambdaUpdateWrapper<Charger> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Charger::getStationId, id)
               .set(Charger::getDeleted, 1);
        chargerService.update(wrapper);

        return R.ok();
    }
}
