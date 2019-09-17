package org.gitflow.sw.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.gitflow.sw.dto.Include;

import java.util.List;

@Mapper
public interface IncludeMapper {

    List<Include> findAll();

}
