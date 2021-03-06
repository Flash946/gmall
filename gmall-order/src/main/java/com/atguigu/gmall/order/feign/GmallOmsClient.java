package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.order.api.GmallOmsApi;
import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("oms-service")
public interface GmallOmsClient extends GmallOmsApi {
}
