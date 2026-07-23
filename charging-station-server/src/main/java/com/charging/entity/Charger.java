package com.charging.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_charger")
public class Charger {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long stationId;
    private String code;
    private String name;
    private Integer type;
    private BigDecimal power;
    @TableField("枪数")
    private Integer gunCount;
    private String image;
    private Integer status;
    private BigDecimal price;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
