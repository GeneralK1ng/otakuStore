package com.otaku.service.impl;

import com.otaku.constant.MessageConstant;
import com.otaku.constant.PasswordConstant;
import com.otaku.constant.StatusConstant;
import com.otaku.context.BaseContext;
import com.otaku.dto.EmployeeDTO;
import com.otaku.dto.EmployeeLoginDTO;
import com.otaku.entity.Employee;
import com.otaku.exception.AccountLockedException;
import com.otaku.exception.AccountNotFoundException;
import com.otaku.exception.PasswordErrorException;
import com.otaku.mapper.EmployeeMapper;
import com.otaku.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //后期需要进行md5加密，然后再进行比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     * @param employeeDTO
     */
    @Override
    public void save(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();

        //对象属性的拷贝（“id”、“username”、“name”、“phone”、“sex”、“idNumber(身份证)”）
        BeanUtils.copyProperties(employeeDTO,employee);

        //设置账号的状态，默认是正常状态“1”,反之“0”表示锁定
        employee.setStatus(StatusConstant.ENABLE);

        //设置密码，默认值是“123456”，后续可以在内部员工自己修改
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

        //设置当前记录的  创建时间 与 修改时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        //设置当前记录的 创建人id 和 修改人id
        // TODO 后期需要改为当前登录用户的id
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.insert(employee);
    }

}
