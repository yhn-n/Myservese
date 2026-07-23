package com.charging.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charging.common.result.R;
import com.charging.entity.Charger;
import com.charging.entity.Order;
import com.charging.entity.Station;
import com.charging.entity.User;
import com.charging.service.ChargerService;
import com.charging.service.OrderService;
import com.charging.service.StationService;
import com.charging.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final StationService stationService;
    private final UserService userService;
    private final OrderService orderService;
    private final ChargerService chargerService;

    @GetMapping("/stats")
    public R<Map<String, Object>> stats() {
        Map<String, Object> stats = new HashMap<>();

        // 总数统计
        stats.put("stationCount", stationService.count());
        stats.put("userCount", userService.count());
        stats.put("orderCount", orderService.count());
        stats.put("chargerCount", chargerService.count());

        // 今日统计
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        LambdaQueryWrapper<Order> todayOrderWrapper = new LambdaQueryWrapper<>();
        todayOrderWrapper.between(Order::getCreateTime, todayStart, todayEnd);
        stats.put("todayOrders", orderService.count(todayOrderWrapper));

        // 在线充电桩数
        LambdaQueryWrapper<Charger> onlineWrapper = new LambdaQueryWrapper<>();
        onlineWrapper.eq(Charger::getStatus, 1).or().eq(Charger::getStatus, 2);
        stats.put("onlineChargers", chargerService.count(onlineWrapper));

        // 运营中站点数
        LambdaQueryWrapper<Station> activeStationWrapper = new LambdaQueryWrapper<>();
        activeStationWrapper.eq(Station::getStatus, 1);
        stats.put("activeStations", stationService.count(activeStationWrapper));

        return R.ok(stats);
    }
}
