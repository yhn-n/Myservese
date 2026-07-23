package com.charging.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_station")
public class Station {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String address;

    private String city;

    private String province;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private Integer status;

    private Integer totalPorts;

    private Integer availablePorts;

    private String operatorId;

    private String phone;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
