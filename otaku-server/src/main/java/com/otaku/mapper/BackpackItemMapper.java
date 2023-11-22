package com.otaku.mapper;

import com.github.pagehelper.Page;
import com.otaku.dto.BagItemPageQueryDTO;
import com.otaku.entity.BagItem;
import com.otaku.vo.BagItemVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    /**
     * 管理端删除背包物品
     *
     * @param ids 物品id
     */
    void deleteBatch(List<Long> ids);

    /**
     * 管理端分页查询
     *
     * @param bagItemPageQueryDTO 查询条件
     * @return 物品分页结果
     */
    Page<BagItemVO> pageQuery(BagItemPageQueryDTO bagItemPageQueryDTO);
}
