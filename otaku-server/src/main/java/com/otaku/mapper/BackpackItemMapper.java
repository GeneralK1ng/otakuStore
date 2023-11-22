package com.otaku.mapper;

import com.otaku.entity.BagItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BackpackItemMapper {

    /**
     * 管理端新增背包物品
     *
     * @param bagItem 背包物品
     */
    @Insert("insert into item_config" +
            "        (image, description, name)" +
            "        values (#{bagItem.image}, #{bagItem.description}, #{bagItem.name})")
    void insert(@Param("bagItem") BagItem bagItem);
}
