package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.ItemGroupVo;
import com.atguigu.gmall.pms.vo.GroupVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;

import java.util.List;

/**
 * 属性分组
 *
 * @author fengge
 * @email fengge@atguigu.com
 * @date 2021-05-13 10:00:52
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    List<GroupVo> queryGroupWithAttrsByCid(Long cartId);

    List<ItemGroupVo> queryGoupsWithAttrValues(Long cid, Long spuId, Long skuId);

}

