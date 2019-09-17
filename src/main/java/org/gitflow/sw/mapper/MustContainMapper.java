package org.gitflow.sw.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.gitflow.sw.dto.MustContain;

import java.util.List;

@Mapper
public interface MustContainMapper {

    List<MustContain> findAll();

}
