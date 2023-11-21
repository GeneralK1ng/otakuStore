package com.otaku.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class CheckinDTO implements Serializable {

    private Long userId;
    private LocalDate checkinDate;
}
