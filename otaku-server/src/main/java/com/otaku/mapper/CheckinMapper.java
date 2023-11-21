package com.otaku.mapper;

import com.otaku.entity.Checkin;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

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
     * @param userId 用户ID
     * @param currentDate 签到日期
     * @return true：已签到，false：未签到
     */
    @Select("select * from checkin where user_id = #{userId} and checkin_date = #{checkinDate}")
    boolean existsCheckinRecordForDate(Long userId, LocalDate currentDate);
}
