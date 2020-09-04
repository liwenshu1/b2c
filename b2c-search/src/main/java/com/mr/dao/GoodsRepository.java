package com.mr.dao;

import com.mr.espojo.Goodes;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface GoodsRepository extends ElasticsearchRepository<Goodes,Long> {
}
