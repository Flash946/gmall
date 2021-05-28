package com.atguigu.gmall.pms.feign;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.vo.SaleVo;
import com.atguigu.gmall.sms.api.GmallSmsApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("sms-service")
//@Component 2021年5月15日18:58:16  我擦这边不写怎么也可以。  老师也没写
// 应该就不用加的啊
public interface GmallSmsClient  extends GmallSmsApi {

//    @PostMapping("sms/skubounds/sale")
//    public ResponseVo saveSales(@RequestBody SaleVo saleVo);
}
