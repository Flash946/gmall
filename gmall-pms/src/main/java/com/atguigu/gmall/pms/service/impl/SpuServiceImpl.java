package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.feign.GmallSmsClient;
import com.atguigu.gmall.pms.mapper.SkuMapper;
import com.atguigu.gmall.pms.mapper.SpuAttrValueMapper;
import com.atguigu.gmall.pms.mapper.SpuDescMapper;
import com.atguigu.gmall.pms.service.*;
import com.atguigu.gmall.pms.vo.SkuVo;
import com.atguigu.gmall.pms.vo.SpuAttrValueVo;
import com.atguigu.gmall.pms.vo.SpuVo;

import com.latguigu.gmall.sms.vo.SaleVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.SpuMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service("spuService")
public class SpuServiceImpl extends ServiceImpl<SpuMapper, SpuEntity> implements SpuService {

    @Autowired
    private SpuDescMapper spuDescMapper;

    @Autowired
    private SpuAttrValueService spuAttrValueService;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuAttrValueService skuAttrValueService;

    @Autowired
    private GmallSmsClient gmallSmsClient;
    @Autowired
    private SpuDescService spuDescService;


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SpuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SpuEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public PageResultVo querySpuByCidPage(PageParamVo paramVo, Long categoryId) {
        QueryWrapper<SpuEntity> wrapper = new QueryWrapper<>();
        //??????id?????????0
        if(categoryId!=0){
            wrapper.eq("category_id",categoryId);
        }
        String key = paramVo.getKey();
        if(StringUtils.isNotBlank(key)){
            wrapper.and(t ->  t.eq("id",key).or().like("name",key));
        }
        IPage<SpuEntity> page = this.page(
                paramVo.getPage(),
                wrapper
        );
        //??????1??? PageResultVo  ???????????????page?????????MP???  ??????????????????????????????
        return new PageResultVo(page);
    }

    @Override
    @Transactional(rollbackFor = FileNotFoundException.class,noRollbackFor = ArithmeticException.class,timeout = 3)
    public void bigSave(SpuVo spuVo) throws FileNotFoundException {

        //1????????????spu????????????
        //1.1?????????spu????????? pms_spu
        this.saveSpu(spuVo);

        //1.2?????????spu??????????????? pms_spu_desc
        this.spuDescService.saveSpuDesc(spuVo);

        //int i = 1/0;

        //new FileInputStream("xx");
        //1.3?????????spu?????????????????????
        this.SaveBaseAttr(spuVo);

        //2?????????sku?????????
        this.saveSku(spuVo);

        this.rabbitTemplate.convertAndSend("pms-item-exchange","item.insert",spuVo.getId());
    }

    public void saveSku(SpuVo spuVo) {
        List<SkuVo> skus = spuVo.getSkus();
        if(CollectionUtils.isEmpty(skus)){
            return;

        }
        skus.forEach(skuVo->{
            //2.1 ??????sku?????????  pms_sku
            skuVo.setSpuId(spuVo.getId());
            skuVo.setBrandId(spuVo.getBrandId());
            skuVo.setCategoryId(spuVo.getCategoryId());

            List<String> images = skuVo.getImages();
            if(!CollectionUtils.isEmpty(images)){  //???????????????????????????????????????
                skuVo.setDefaultImage(CollectionUtils.isEmpty(images)?skuVo.getDefaultImage():images.get(0));

            }
            this.skuMapper.insert(skuVo);
            //2.2 ??????sku??????????????? pms_sku_images  ????????????sku???id???????????????????????????
            if(!CollectionUtils.isEmpty(images)){  //???????????????????????????????????????

                List<SkuImagesEntity> skuImageEntities = images.stream().map(url -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuVo.getId());
                    skuImagesEntity.setUrl(url);
                    skuImagesEntity.setSort(0);
                    skuImagesEntity.setDefaultStatus(StringUtils.equals(skuVo.getDefaultImage(), url) ? 1 : 0);
                    return skuImagesEntity;
                }).collect(Collectors.toList());
                skuImagesService.saveBatch(skuImageEntities);

            }
            //2.3 ??????sku??????????????? pms_sku_attr_value
            List<SkuAttrValueEntity> saleAttrs = skuVo.getSaleAttrs();
            if(!CollectionUtils.isEmpty(saleAttrs)){  //???????????????????????????????????????

               saleAttrs.forEach(saleAttr ->{
                   saleAttr.setSkuId(skuVo.getId());
               });
                this.skuAttrValueService.saveBatch(saleAttrs);
            }

           SaleVo saleVo = new SaleVo();
            BeanUtils.copyProperties(skuVo,saleVo);
            saleVo.setSkuId(skuVo.getId());
            gmallSmsClient.saveSales(saleVo);
        });
    }

    private void SaveBaseAttr(SpuVo spuVo) {
        List<SpuAttrValueVo> baseAttrs = spuVo.getBaseAttrs();
        if(!CollectionUtils.isEmpty(baseAttrs)){

            List<SpuAttrValueEntity> spuAttrValueEntities = baseAttrs.stream().map(spuAttrValueVo -> {
                SpuAttrValueEntity spuAttrValueEntity = new SpuAttrValueEntity();
                BeanUtils.copyProperties(spuAttrValueVo, spuAttrValueEntity);
                spuAttrValueEntity.setSpuId(spuVo.getId());
                return spuAttrValueEntity;
            }).collect(Collectors.toList());
            //??????????????????  ???????????????entity??????
            this.spuAttrValueService.saveBatch(spuAttrValueEntities);
        }
    }


    private void saveSpu(SpuVo spuVo) {
        spuVo.setCreateTime(new Date());
        spuVo.setUpdateTime(spuVo.getCreateTime());
        this.save(spuVo);
    }

}