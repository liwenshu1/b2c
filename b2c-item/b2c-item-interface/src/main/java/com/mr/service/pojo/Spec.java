package com.mr.service.pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Table(name="tb_spec_group")
public class Spec {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long cid;

    private String name;

    @Transient
    private List<SpecParam>  SpecParamList;

}
