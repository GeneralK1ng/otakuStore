package com.otaku.service;

public interface CheckinService {
    /**
     * 用户签到
     *
     * @param userId 用户ID
     */
    void checkin(Long userId);
}
