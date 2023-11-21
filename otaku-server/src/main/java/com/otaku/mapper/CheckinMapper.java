package com.otaku.mapper;

import com.otaku.dto.CheckinDTO;
import com.otaku.entity.Checkin;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface CheckinMapper {
    
    /**
     * 签到
     * @param checkin 签到记录
     */
    @Insert("INSERT INTO checkin(user_id, checkin_time, checkin_date) " +
            "values " +
            "(#{userId}, #{checkinTime}, #{checkinDate})")
    void insertCheckinRecord(Checkin checkin);

    /**
     * 判断用户是否签到
     * @param checkinDTO 签到记录
     * @return 签到记录
     */
    @Select("select * from checkin where user_id = #{checkinDTO.userId} and checkin_date = #{checkinDTO.checkinDate}")
    Checkin existsCheckinRecordForDate(@Param("checkinDTO") CheckinDTO checkinDTO);

    /**
     * 获取用户签到日期
     * @param userId 用户ID
     * @return 签到日期
     */
    List<LocalDate> getCheckinDates(Long userId);
}
