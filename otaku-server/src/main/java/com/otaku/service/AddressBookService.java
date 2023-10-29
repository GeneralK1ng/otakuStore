package com.otaku.service;

import com.otaku.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    /**
     * 查询当前用户的地址信息
     * @param addressBook
     * @return
     */
    List<AddressBook> list(AddressBook addressBook);

    /**
     * 新增地址
     * @param addressBook
     */
    void save(AddressBook addressBook);

    /**
     * 根据ID查询地址
     * @param id
     * @return
     */
    AddressBook getById(Long id);

    /**
     * 根据ID修改地址
     * @param addressBook
     */
    void update(AddressBook addressBook);

    /**
     * 设置默认地址
     * @param addressBook
     */
    void setDefault(AddressBook addressBook);

    /**
     *根据ID删除地址信息
     * @param id
     */
    void deleteById(Long id);
}
