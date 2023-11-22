package com.otaku.service.impl;

import com.otaku.constant.StatusConstant;
import com.otaku.dto.CheckinDTO;
import com.otaku.dto.UserPointRewardDTO;
import com.otaku.entity.Checkin;
import com.otaku.entity.UserBackpack;
import com.otaku.exception.AlreadyCheckinException;
import com.otaku.exception.UserDoesNotExistException;
import com.otaku.mapper.CheckinMapper;
import com.otaku.mapper.BackpackMapper;
import com.otaku.mapper.UserMapper;
import com.otaku.service.CheckinService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class CheckinServiceImpl implements CheckinService {

    @Autowired
    private CheckinMapper checkinMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BackpackMapper backpackMapper;

    /**
     * 用户签到
     * @param userId 用户ID
     */
    @Override
    @Transactional
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
            // 如果用户当天没有签到过，则进行签到操作
            checkin = new Checkin();
            checkin.setUserId(userId);
            checkin.setCheckinDate(currentDate);
            checkin.setCheckinTime(LocalTime.now());

            // 插入签到记录到数据库
            checkinMapper.insertCheckinRecord(checkin);

            // 进行签到后的其他逻辑，比如给用户发放积分等
            issuePointReward(userId);

        } else {
            log.warn("用户 {} 已经在 {} 签到过，不能重复签到", userId , currentDate);

            // 抛出异常，提示用户不能重复签到
            throw new AlreadyCheckinException("用户已经签到过，不能重复签到");
        }
    }

    /**
     * 签到后奖励积分
     * @param userId 用户ID
     */
    private void issuePointReward(Long userId){
        // 在数据库中查询用户的签到信息，并且计算连续的签到天数
        int consecutiveDays = calculateConsecutiveDays(userId);

        // 根据奖励规则计算当天的积分奖励
        int pointReward = calculatePointReward(consecutiveDays);

        // TODO 判断是否是特殊节日，如果是则赠送额外积分

        // TODO 判断是否是用户纪念日

        // 创建添加积分对象
        UserPointRewardDTO userPointRewardDTO = new UserPointRewardDTO();
        userPointRewardDTO.setUserId(userId);
        userPointRewardDTO.setQuantity(pointReward);

        // 创建背包数据对象
        UserBackpack userBackpack = new UserBackpack();

        // 将积分奖励对象转换为背包数据对象
        BeanUtils.copyProperties(userPointRewardDTO, userBackpack);
        userBackpack.setItemId(UserPointRewardDTO.itemId);
        userBackpack.setStatus(StatusConstant.DEFAULT);

        String idempotentId = UUID.randomUUID().toString();
        userBackpack.setIdempotent(idempotentId);

        // 给用户增加积分
        // 添加一条背包数据
        backpackMapper.insertPoint(userBackpack);
        log.info("用户 {} 签到成功，连续签到天数 {}，积分奖励 {}，积分记录ID {}", userId, consecutiveDays, pointReward, idempotentId);
    }

    /**
     * 计算积分奖励
     * @param consecutiveDays 连续签到天数
     * @return 积分奖励
     */
    private int calculatePointReward(int consecutiveDays) {
        // 根据连续签到天数计算积分奖励的逻辑
        if (consecutiveDays >= 0 && consecutiveDays <= 5) {
            return 5;
        } else if (consecutiveDays <= 10) {
            return 10;
        } else if (consecutiveDays <= 15) {
            return 15;
        } else {
            return 20;
        }
    }

    /**
     * 计算连续签到天数
     * @param userId 用户ID
     * @return 连续签到天数
     */
    private int calculateConsecutiveDays(Long userId) {
        // 查询用户的签到记录并按日期排序
        List<LocalDate> checkinDates = checkinMapper.getCheckinDates(userId);

        // 如果没有签到记录，则返回 0
        if (checkinDates.isEmpty()) {
            return 0;
        }

        // 计算连续签到天数
        int consecutiveDays = 0;
        LocalDate previousDate = checkinDates.get(0);

        for (int i = 1; i < checkinDates.size(); i++) {
            LocalDate currentDate = checkinDates.get(i);

            // 判断当前日期是否是前一天的后一天，如果是则连续签到天数加一，否则重新开始计算
            if (currentDate.minusDays(1).isEqual(previousDate)) {
                consecutiveDays++;
            } else {
                consecutiveDays = 1;
            }
            previousDate = currentDate;
        }

        return consecutiveDays;
    }
}
