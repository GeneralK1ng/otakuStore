package com.otaku.service.impl;

import com.otaku.dto.CheckinDTO;
import com.otaku.entity.Checkin;
import com.otaku.exception.AlreadyCheckinException;
import com.otaku.exception.UserDoesNotExistException;
import com.otaku.mapper.CheckinMapper;
import com.otaku.mapper.UserMapper;
import com.otaku.service.CheckinService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@Slf4j
public class CheckinServiceImpl implements CheckinService {

    @Autowired
    private CheckinMapper checkinMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void checkin(Long userId) {
        // 获取当前的日期
        LocalDate currentDate = LocalDate.now();

        if (userMapper.getById(userId) == null) {
            throw new UserDoesNotExistException("该用户不存在");
        }

        // 根据 userId 和当前日期查询用户当日是否已经签到
        CheckinDTO checkinDTO = new CheckinDTO();
        checkinDTO.setUserId(userId);
        checkinDTO.setCheckinDate(currentDate);

        Checkin checkin = checkinMapper.existsCheckinRecordForDate(checkinDTO);
        boolean isCheckin = checkin!= null;

        if (!isCheckin) {
            // 如果用户没有签到过，则进行签到操作
            checkin = new Checkin();
            checkin.setUserId(userId);
            checkin.setCheckinDate(currentDate);
            checkin.setCheckinTime(LocalTime.now());

            // 插入签到记录到数据库
            checkinMapper.insertCheckinRecord(checkin);

            // TODO 进行签到后的其他逻辑，比如给用户发放积分等

        } else {
            log.warn("用户 {} 已经在 {} 签到过，不能重复签到", userId , currentDate);

            // 抛出异常，提示用户不能重复签到
            throw new AlreadyCheckinException("用户已经签到过，不能重复签到");
        }
    }
}
