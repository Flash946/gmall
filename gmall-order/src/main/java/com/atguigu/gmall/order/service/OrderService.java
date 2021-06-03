package com.atguigu.gmall.order.service;

import com.atguigu.gmall.cart.bean.Cart;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.common.bean.UserInfo;
import com.atguigu.gmall.common.exception.OrderException;
import com.atguigu.gmall.order.feign.*;
import com.atguigu.gmall.order.interceptor.LoginInterceptor;
import com.atguigu.gmall.order.vo.OrderConfirmVo;
import com.atguigu.gmall.order.vo.OrderItemVo;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SkuEntity;
import com.atguigu.gmall.ums.entity.UserAddressEntity;
import com.atguigu.gmall.ums.entity.UserEntity;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.latguigu.gmall.sms.vo.ItemSaleVo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private GmallWmsClient wmsClient;

    @Autowired
    private GmallPmsClient pmsClient;

    @Autowired
    private GmallSmsClient smsClient;

    @Autowired
    private GmallUmsClient umsClient;

    @Autowired
    private GmallOmsClient omsClient;

    @Autowired
    private GmallCartClient cartClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String KEY_PREFIX = "order:token:";

    public OrderConfirmVo confirm() {
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        //查询送货清单
        ResponseVo<List<Cart>> cartListRspVo = this.cartClient.queryCheckCarts(userInfo.getUserId());
        List<Cart> carts = cartListRspVo.getData();
        if(CollectionUtils.isEmpty(carts)){
            throw new OrderException("没有选中购物车");
        }

       List<OrderItemVo> orderItemVos = carts.stream().map(cart -> {
            OrderItemVo orderItemVo = new OrderItemVo();
            orderItemVo.setSkuId(cart.getSkuId());
            orderItemVo.setCount(cart.getCount());

            //根据skuId查询sku
            ResponseVo<SkuEntity> skuEntityResponseVo = this.pmsClient.querySkuById(cart.getSkuId());
            SkuEntity skuEntity = skuEntityResponseVo.getData();
            orderItemVo.setTitle(skuEntity.getTitle());
            orderItemVo.setPrice(skuEntity.getPrice());
            orderItemVo.setDefaultImage(skuEntity.getDefaultImage());
            orderItemVo.setWeight(new BigDecimal(skuEntity.getWeight()) );

            //查询销售熟悉

            ResponseVo<List<SkuAttrValueEntity>> listResponseVo = this.pmsClient.querySkuAttrValuesBySkuId(skuEntity.getId());
            List<SkuAttrValueEntity> skuAttrValueEntities = listResponseVo.getData();
            orderItemVo.setSaleAttrs(skuAttrValueEntities);

            //根据skuId查询营销信息
            ResponseVo<List<ItemSaleVo>> listResponseVo1 = this.smsClient.querySalesBySkuId(cart.getSkuId());
            List<ItemSaleVo> itemSaleVos = listResponseVo1.getData();

            orderItemVo.setSales(itemSaleVos);

            //根据skuId查询库存信息
            ResponseVo<List<WareSkuEntity>> listResponseVo2 = this.wmsClient.queryWareSkusBySkuId(cart.getSkuId());
            List<WareSkuEntity> wareSkuEntities = listResponseVo2.getData();
            if(CollectionUtils.isEmpty(wareSkuEntities)){
                ;
                orderItemVo.setStore(wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock()-wareSkuEntity.getStockLocked()>0));
            }
            return  orderItemVo;
        }).collect(Collectors.toList());
        confirmVo.setItems(orderItemVos);

        //查询收货地址列表
        ResponseVo<List<UserAddressEntity>> listResponseVo = this.umsClient.queryAddressesByUserId(userInfo.getUserId());
        List<UserAddressEntity> addresses = listResponseVo.getData();
        confirmVo.setAddresses(addresses);

        //查询用户积分信息
        ResponseVo<UserEntity> userEntityResponseVo = this.umsClient.queryUserById(userInfo.getUserId());
        UserEntity userEntity = userEntityResponseVo.getData();
        if(userEntity!=null){

            confirmVo.setBounds(userEntity.getIntegration());
        }
        //防重唯一标识
        String timeId = IdWorker.getTimeId();
        this.redisTemplate.opsForValue().set(KEY_PREFIX+timeId,timeId);
        confirmVo.setOrderToken(timeId);
        return  confirmVo;

    }
}
