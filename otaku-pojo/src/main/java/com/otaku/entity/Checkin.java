package com.otaku.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Checkin {
    // 主键
    private Long id;

    // 用户id
    private Long userId;

    // 签到时间
    private LocalTime checkinTime;

    // 签到日期
    private LocalDate checkinDate;
}
