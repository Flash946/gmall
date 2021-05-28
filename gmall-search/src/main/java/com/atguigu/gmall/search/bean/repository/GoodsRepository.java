package com.atguigu.gmall.search.bean.repository;

import com.atguigu.gmall.search.bean.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {
}
