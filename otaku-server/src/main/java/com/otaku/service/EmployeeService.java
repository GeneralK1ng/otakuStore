package com.otaku.service;

import com.otaku.dto.EmployeeLoginDTO;
import com.otaku.entity.Employee;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

}
