package com.charging.controller.miniapp;

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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "小程序端-订单接口")
@RestController
@RequestMapping("/miniapp/order")
@RequiredArgsConstructor
public class MiniOrderController {

    private final OrderService orderService;
    private final ChargerService chargerService;
    private final StationService stationService;
    private final UserService userService;

    @Operation(summary = "创建充电订单")
    @PostMapping("/create")
    public R<Map<String, Object>> create(@RequestAttribute Long userId, @RequestBody Map<String, Object> params) {
        Long stationId = Long.valueOf(params.get("stationId").toString());
        Long chargerId = Long.valueOf(params.get("chargerId").toString());
        Long gunId = Long.valueOf(params.get("gunId").toString());

        Charger charger = chargerService.getById(chargerId);
        if (charger == null || charger.getDeleted() == 1) {
            return R.error("充电桩不存在");
        }
        Station station = stationService.getById(stationId);
        if (station == null || station.getDeleted() == 1) {
            return R.error("充电站不存在");
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setStationId(stationId);
        order.setChargerId(chargerId);
        order.setGunId(gunId);
        order.setOrderNo("ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        order.setStartTime(LocalDateTime.now());
        order.setPrice(charger.getPrice() != null ? charger.getPrice() : new BigDecimal("1.20"));
        order.setElectricity(BigDecimal.ZERO);
        order.setTotalAmount(BigDecimal.ZERO);
        order.setPayAmount(BigDecimal.ZERO);
        order.setStatus(1);
        orderService.save(order);

        return R.ok(buildOrderVo(order, station, charger));
    }

    @Operation(summary = "获取用户订单列表")
    @GetMapping("/list")
    public R<Page<Map<String, Object>>> list(
            @RequestAttribute Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        Page<Order> page = orderService.page(new Page<>(pageNum, pageSize), wrapper);

        List<Long> stationIds = page.getRecords().stream().map(Order::getStationId).distinct().collect(Collectors.toList());
        Map<Long, Station> stationMap = stationIds.isEmpty() ? new HashMap<>() :
                stationService.listByIds(stationIds).stream().collect(Collectors.toMap(Station::getId, s -> s));

        List<Map<String, Object>> records = page.getRecords().stream().map(order -> {
            Station station = stationMap.get(order.getStationId());
            return buildOrderVo(order, station, null);
        }).collect(Collectors.toList());

        Page<Map<String, Object>> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(records);
        return R.ok(result);
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/{id:[0-9]+}")
    public R<Map<String, Object>> detail(@PathVariable Long id, @RequestAttribute Long userId) {
        Order order = orderService.getById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            return R.error("订单不存在");
        }
        Station station = stationService.getById(order.getStationId());
        Charger charger = chargerService.getById(order.getChargerId());
        return R.ok(buildOrderVo(order, station, charger));
    }

    @Operation(summary = "结束充电/完成订单")
    @PutMapping("/{id:[0-9]+}/finish")
    public R<Void> finish(@PathVariable Long id, @RequestAttribute Long userId) {
        Order order = orderService.getById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            return R.error("订单不存在");
        }
        if (order.getStatus() != 1) {
            return R.error("订单状态异常");
        }

        LocalDateTime endTime = LocalDateTime.now();
        order.setEndTime(endTime);
        long seconds = ChronoUnit.SECONDS.between(order.getStartTime(), endTime);
        order.setDuration((int) seconds);

        // 模拟电量：每秒 0.033 度（约 2 度/分钟）
        BigDecimal electricity = BigDecimal.valueOf(seconds)
                .multiply(new BigDecimal("0.033"))
                .setScale(2, RoundingMode.HALF_UP);
        order.setElectricity(electricity);

        BigDecimal totalAmount = electricity.multiply(order.getPrice()).setScale(2, RoundingMode.HALF_UP);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);
        order.setPayType(1);
        order.setStatus(2);

        // 从用户余额扣款
        User user = userService.getById(userId);
        if (user == null) {
            return R.error("用户不存在");
        }
        if (user.getBalance().compareTo(totalAmount) < 0) {
            return R.error("余额不足，请先充值");
        }
        user.setBalance(user.getBalance().subtract(totalAmount));
        userService.updateById(user);

        orderService.updateById(order);
        return R.ok();
    }

    @Operation(summary = "取消订单")
    @PutMapping("/{id:[0-9]+}/cancel")
    public R<Void> cancel(@PathVariable Long id, @RequestAttribute Long userId) {
        Order order = orderService.getById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            return R.error("订单不存在");
        }
        if (order.getStatus() != 1) {
            return R.error("只有充电中的订单可取消");
        }
        order.setStatus(3);
        orderService.updateById(order);
        return R.ok();
    }

    private Map<String, Object> buildOrderVo(Order order, Station station, Charger charger) {
        Map<String, Object> vo = new HashMap<>();
        vo.put("id", order.getId());
        vo.put("orderNo", order.getOrderNo());
        vo.put("userId", order.getUserId());
        vo.put("stationId", order.getStationId());
        vo.put("stationName", station != null ? station.getName() : "充电站");
        vo.put("chargerId", order.getChargerId());
        vo.put("chargerName", charger != null ? charger.getName() : null);
        vo.put("gunId", order.getGunId());
        vo.put("startTime", order.getStartTime());
        vo.put("endTime", order.getEndTime());
        vo.put("duration", order.getDuration());
        vo.put("electricity", order.getElectricity());
        vo.put("price", order.getPrice());
        vo.put("totalAmount", order.getTotalAmount());
        vo.put("payAmount", order.getPayAmount());
        vo.put("payType", order.getPayType());
        vo.put("status", order.getStatus());
        vo.put("createTime", order.getCreateTime());
        return vo;
    }
}
