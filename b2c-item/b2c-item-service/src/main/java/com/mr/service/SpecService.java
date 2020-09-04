package com.mr.service;

import com.mr.mapper.SpecMapper;
import com.mr.mapper.SpecParamMapper;
import com.mr.service.pojo.Spec;
import com.mr.service.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class SpecService {

    @Autowired
    private SpecMapper mapper;

    @Autowired
    private SpecParamMapper ParamMapper;


    public List<Spec>  list(Long cid){
        Example example = new Example(Spec.class);

        example.createCriteria().andEqualTo("cid",cid);

        return mapper.selectByExample(example);
    }


    public List<SpecParam> ParamList(Long gid){
        SpecParam param = new SpecParam();
        param.setGroupId(gid);

        return ParamMapper.select(param);
    }

    public int save(Spec spec){

        return mapper.insertSelective(spec);
    }
    public int saveParam(SpecParam specParam){

        return ParamMapper.insertSelective(specParam);
    }

    public int delete(Long id){

        return mapper.deleteByPrimaryKey(id);
    }

    public int deleteParam(Long id){

        return ParamMapper.deleteByPrimaryKey(id);
    }

    public int put(Spec spec){

        return mapper.updateByPrimaryKeySelective(spec);
    }
    public int putParam(SpecParam specParam){

        return ParamMapper.updateByPrimaryKeySelective(specParam);
    }


    //商品列表 的 规格查询

    public List<SpecParam> querySpecByCid(Long cid,Long gid,Boolean searching,Boolean generic){
        SpecParam param = new SpecParam();
        param.setCid(cid);
        param.setGroupId(gid);
        param.setSearching(searching);
        param.setGeneric(generic);
        return ParamMapper.select(param);
    }
}
