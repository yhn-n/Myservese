package com.charging.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/order")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final StationService stationService;
    private final ChargerService chargerService;

    @GetMapping("/statistics")
    public R<Map<String, Object>> statistics() {
        Map<String, Object> data = new HashMap<>();

        // 总订单数
        data.put("totalOrders", orderService.count());

        // 总用户数
        data.put("totalUsers", userService.count());

        // 站点数量
        data.put("totalStations", stationService.count());

        // 总收入（已完成订单的 payAmount 之和）
        LambdaQueryWrapper<Order> paidWrapper = new LambdaQueryWrapper<>();
        paidWrapper.eq(Order::getStatus, 2);
        List<Order> paidOrders = orderService.list(paidWrapper);
        BigDecimal totalRevenue = paidOrders.stream()
                .map(o -> o.getPayAmount() != null ? o.getPayAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        data.put("totalRevenue", totalRevenue);

        // 近7天数据
        List<String> dates = new ArrayList<>();
        List<Integer> orderCounts = new ArrayList<>();
        List<BigDecimal> revenues = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            dates.add(date.format(formatter));

            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.atTime(LocalTime.MAX);

            LambdaQueryWrapper<Order> dayWrapper = new LambdaQueryWrapper<>();
            dayWrapper.between(Order::getCreateTime, dayStart, dayEnd);
            List<Order> dayOrders = orderService.list(dayWrapper);

            orderCounts.add(dayOrders.size());

            BigDecimal dayRevenue = dayOrders.stream()
                    .map(o -> o.getPayAmount() != null ? o.getPayAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            revenues.add(dayRevenue);
        }
        data.put("dates", dates);
        data.put("orderCounts", orderCounts);
        data.put("revenues", revenues);

        // 充电类型分布
        List<Charger> chargers = chargerService.list();
        int dcFast = 0, acSlow = 0;
        for (Charger c : chargers) {
            if (c.getType() != null && c.getType() == 1) {
                dcFast++;
            } else if (c.getType() != null && c.getType() == 2) {
                acSlow++;
            }
        }
        data.put("dcFast", dcFast);
        data.put("acSlow", acSlow);

        return R.ok(data);
    }

    @GetMapping("/list")
    public R<Page<Order>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Order::getOrderNo, keyword);
        }
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        return R.ok(orderService.page(new Page<>(pageNum, pageSize), wrapper));
    }

    @GetMapping("/{id}")
    public R<Order> detail(@PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order == null || order.getDeleted() == 1) return R.error("订单不存在");
        return R.ok(order);
    }
}
