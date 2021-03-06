package com.mr.service.api;

import com.mr.service.pojo.Spec;
import com.mr.service.pojo.SpecParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("spec")
public interface SpecApi {

    @GetMapping("groups/{cid}")
    public List<Spec> groups(@PathVariable("cid") Long cid);

    @GetMapping("params")
    public List<SpecParam> params(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "cid",required = false) Long cid,
            @RequestParam(value = "searching",required = false) Boolean searching,
            @RequestParam(value = "generic",required = false) Boolean generic);

}
