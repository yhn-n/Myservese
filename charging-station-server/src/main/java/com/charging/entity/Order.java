package com.charging.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_order")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long userId;

    private Long stationId;

    private Long chargerId;

    private Long gunId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer duration;

    private BigDecimal electricity;

    private BigDecimal price;

    private BigDecimal totalAmount;

    private BigDecimal payAmount;

    private Integer payType;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
